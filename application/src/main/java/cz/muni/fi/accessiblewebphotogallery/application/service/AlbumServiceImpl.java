package cz.muni.fi.accessiblewebphotogallery.application.service;

import cz.muni.fi.accessiblewebphotogallery.application.ApplicationConfig;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.AlbumService;
import cz.muni.fi.accessiblewebphotogallery.persistence.dao.AlbumDao;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AlbumEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;

@Service
public class AlbumServiceImpl implements AlbumService {

    private Logger log = LogManager.getLogger(AlbumServiceImpl.class);
    private AlbumDao albumDao;
    private ApplicationConfig config;
    private Random rng;
    private Base64.Encoder enc;
    private MessageDigest hasher;

    @Autowired
    public AlbumServiceImpl(AlbumDao albumDao, ApplicationConfig config) {
        this.albumDao = albumDao;
        this.config = config;
        rng = new Random();
        enc = Base64.getUrlEncoder();
        hasher = null;
    }

    @Override
    public List<AlbumEntity> findAll() {
        return albumDao.findAll();
    }

    @Override
    public List<AlbumEntity> findByAlbumOwner(UserEntity owner) {
        return albumDao.findByAlbumOwner(owner);
    }

    @Override
    public AlbumEntity createAlbum(UserEntity user, String albumName) {
        Validate.notNull(user);
        Validate.notNull(albumName);
        Instant creationTime = Instant.now();
        String base64;
        File albumDir = new File(config.getAlbumDirectory());
        if (!albumDir.exists()) {
            boolean created = albumDir.mkdirs();
            if (!created) {
                log.error("Could not create album directory(did not exist). Aborting.");
                return null;
            }
        }
        if (hasher == null) {
            try {
                hasher = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                log.catching(e);
                log.error(e.getMessage());
                log.error("Could not obtain a MessageDigest instance. Aborting.");
                return null;
            }
        }
        List<ByteBuffer> toHash = new ArrayList<>();
        toHash.add(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(user.hashCode()));
        toHash.add(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(creationTime.hashCode()));
        do {
            toHash.add(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(rng.nextInt()));
            base64 = computeBase64(toHash);
        } while (albumDao.findById(base64).isPresent());
        AlbumEntity toSave = new AlbumEntity();
        toSave.setAlbumName(albumName);
        toSave.setAlbumOwner(user);
        toSave.setId(base64);
        File albumFile = new File(albumDir, base64 + ".txt");
        try {
            boolean created = albumFile.createNewFile();
            if (!created) {
                log.error("Failed to create album file. Aborting.");
                return null;
            }
        } catch (IOException e) {
            log.catching(e);
            log.error(e.getMessage());
            log.error("Failed to create album file(IOException). Aborting.");
            return null;
        }
        return albumDao.save(toSave);
    }

    @Override
    public AlbumEntity updateAlbum(AlbumEntity album) {
        return albumDao.save(album);
    }

    @Override
    public void deleteAlbum(AlbumEntity album) {
        File albumFile = new File(config.getAlbumDirectory() + File.separator + album.getId() + ".txt");
        try {
            Files.delete(albumFile.toPath());
        } catch (IOException e) {
            log.catching(e);
            log.error(e.getMessage());
            log.error("IOException deleting album file of album:" + album + " . Aborting.");
            return;
        }
        albumDao.delete(album);
    }

    @Override
    public boolean addPhotoToAlbum(AlbumEntity album, String photoId) {
        File albumFile = new File(config.getAlbumDirectory() + File.separator + album.getId() + ".txt");
        if (!albumFile.exists()) {
            log.error("Attempting to add photo: " + photoId + " to album:" + album + " failed: album file doesn't seem to exist. Aborting");
            return false;
        }
        photoId += System.lineSeparator();
        try {
            Files.write(albumFile.toPath(), photoId.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE, StandardOpenOption.APPEND, StandardOpenOption.SYNC);
        } catch (IOException e) {
            log.catching(e);
            log.error(e.getMessage());
            log.error("Attempting to add photo: " + photoId + " to album:" + album + " failed(IOException). Aborting");
            return false;
        }
        return true;
    }

    @Override
    public boolean removePhotoFromAlbum(AlbumEntity album, String photoId) {
        File albumFile = new File(config.getAlbumDirectory() + File.separator + album.getId() + ".txt");
        if (!albumFile.exists()) {
            log.error("Attempting to remove photo: " + photoId + " from album:" + album + " failed: album file doesn't seem to exist. Aborting");
            return false;
        }
        List<String> photoList;
        try {
            photoList = Files.readAllLines(albumFile.toPath());
        } catch (IOException e) {
            log.catching(e);
            log.error(e.getMessage());
            log.error("Attempting to remove photo: " + photoId + " from album:" + album + " failed reading file(IOException). Aborting");
            return false;
        }
        photoList.remove(photoId);
        try {
            Files.write(albumFile.toPath(), photoList, StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.SYNC);
        } catch (IOException e) {
            log.catching(e);
            log.error(e.getMessage());
            log.error("Attempting to remove photo: " + photoId + " from album:" + album + " failed writing file(IOException). Aborting");
            return false;
        }
        return true;
    }

    @Override
    public List<String> listPhotosInAlbum(AlbumEntity album) {
        List<String> photoList;
        File albumFile = new File(config.getAlbumDirectory() + File.separator + album.getId() + ".txt");
        if (!albumFile.exists()) {
            log.error("Attempting to list photos from album:" + album + " failed: album file doesn't seem to exist. Aborting");
            return null;
        }
        try {
            photoList = Files.readAllLines(albumFile.toPath());
        } catch (IOException e) {
            log.catching(e);
            log.error(e.getMessage());
            log.error("Attempting to list photos from album:" + album + " failed reading file(IOException). Aborting");
            return null;
        }
        return photoList;
    }

    @Override
    public Optional<AlbumEntity> findById(String base64) {
        return albumDao.findById(base64);
    }

    private String computeBase64(List<ByteBuffer> data) {
        hasher.reset();
        for (ByteBuffer buf : data) {
            hasher.update(buf.array());
        }
        byte[] hashResult = hasher.digest();
        byte[] dbHash = Arrays.copyOfRange(hashResult, 1, 16);
        return enc.encodeToString(dbHash);
    }
}
