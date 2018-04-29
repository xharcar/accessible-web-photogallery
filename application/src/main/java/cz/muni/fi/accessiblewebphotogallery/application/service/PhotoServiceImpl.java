package cz.muni.fi.accessiblewebphotogallery.application.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.PhotoService;
import cz.muni.fi.accessiblewebphotogallery.persistence.dao.PhotoDao;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class PhotoServiceImpl implements PhotoService {

    private static final Logger log = LogManager.getLogger(PhotoServiceImpl.class);
    private PhotoDao photoDao;
    private Base64.Encoder enc;
    private MessageDigest hasher;
    private Random rng;
    private Gson jsonConverter;

    @Inject
    public PhotoServiceImpl(PhotoDao photoDao) {
        this.photoDao = photoDao;
        enc = Base64.getUrlEncoder();
        rng = new Random();
        hasher = null;
        jsonConverter = new Gson();
    }

    @Override
    public PageImpl<PhotoEntity> findAll(Pageable pageable) {
        Page<PhotoEntity> page = photoDao.findAll(pageable);
        return new PageImpl<>(page.getContent(),pageable,page.getTotalElements());
    }

    @Override
    public PageImpl<PhotoEntity> findByUploadTimeBetween(Instant begin, Instant end, Pageable pageable) {
        Page<PhotoEntity> page = photoDao.findByUploadTimeBetween(begin, end, pageable);
        return new PageImpl<>(page.getContent(),pageable,page.getTotalElements());
    }

    @Override
    public PageImpl<PhotoEntity> findByUploader(UserEntity uploader, Pageable pageable) {
        Page<PhotoEntity> page = photoDao.findByUploader(uploader, pageable);
        return new PageImpl<>(page.getContent(),pageable,page.getTotalElements());
    }

    @Override
    public PageImpl<PhotoEntity> findByDescPartIgnoreCase(String searchStr, Pageable pageable) {
        Page<PhotoEntity> page = photoDao.findByDescriptionContainingIgnoreCase(searchStr,pageable);
        return new PageImpl<>(page.getContent(),pageable,page.getTotalElements());
    }

    @Override
    public PageImpl<PhotoEntity> findByTitlePartIgnoreCase(String searchStr, Pageable pageable) {
        Page<PhotoEntity> page = photoDao.findByTitleContainingIgnoreCase(searchStr,pageable);
        return new PageImpl<>(page.getContent(),pageable,page.getTotalElements());
    }

    @Override
    public Optional<PhotoEntity> findByBase64Id(String b64id) {
        return photoDao.findByBase64Identifier(b64id);
    }

    @Override
    public PageImpl<PhotoEntity> findNewestFirst(Pageable pageable) {
        Page<PhotoEntity> page = photoDao.findAllByOrderByUploadTimeDesc(pageable);
        return new PageImpl<>(page.getContent(),pageable,page.getTotalElements());
    }

    @Override
    public PhotoEntity registerPhoto(PhotoEntity entity, File photoFile, File metadataFile) {
        Validate.notNull(entity);
        Validate.notNull(photoFile);
        Validate.isTrue(photoFile.exists());
        // entity should have uploader, title and description set by now
        Instant uploadTime = Instant.now();
        String photoBase64;
        if(hasher == null){
            try {
                hasher = MessageDigest.getInstance("MD5");
                // we don't need a cryptographically secure hash for base-64 identifiers
            } catch (NoSuchAlgorithmException nsae) {
                // this should never occur, but I know better than that...
                log.catching(nsae);
                log.error("Could not obtain an MD5 MessageDigest instance.");
                return null;
            }
        }
        List<ByteBuffer> extras = new ArrayList<>();
        extras.add(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(entity.getUploader().hashCode()));
        extras.add(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(uploadTime.hashCode()));
        do {
            extras.add(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(rng.nextInt()));
            // generating one extra int isn't that much unnecessary work, and it allows for a neat
            // do-while loop rather than a call followed by a while-looped call
            photoBase64 = computeBase64(photoFile, metadataFile, extras);
            if (photoBase64 == null) return null;
        } while (photoDao.findByBase64Identifier(photoBase64).isPresent());
        entity.setUploadTime(uploadTime);
        entity.setBase64Identifier(photoBase64);
        Metadata exif = null;
        try {
            exif = ImageMetadataReader.readMetadata(photoFile);
        } catch (ImageProcessingException | IOException e) {
            log.catching(e);
            log.error("Couldn't extract EXIF metadata from photo.");
        }
        if (exif != null) {
            Directory exifDir = exif.getFirstDirectoryOfType(ExifDirectoryBase.class);
            String camMake = exifDir.getString(ExifDirectoryBase.TAG_MAKE);
            String camModel = exifDir.getString(ExifDirectoryBase.TAG_MODEL);
            StringBuilder cameraStrBuilder = new StringBuilder();
            if (camMake == null) {
                cameraStrBuilder.append("");
            } else {
                cameraStrBuilder.append(camMake);
                cameraStrBuilder.append(" ");
            }
            if (camModel == null) {
                cameraStrBuilder.append("");
            } else {
                cameraStrBuilder.append(camModel);
            }
            if (!cameraStrBuilder.toString().isEmpty()) {
                entity.setCameraModel(cameraStrBuilder.toString());
            } else {
                entity.setCameraModel(null);
            }
            entity.setExposureTime(exifDir.getDoubleObject(ExifDirectoryBase.TAG_EXPOSURE_TIME));
            entity.setDatetimeTaken(null);
            java.util.Date exifDate = exifDir.getDate(ExifDirectoryBase.TAG_DATETIME);
            if (exifDate != null) {
                entity.setDatetimeTaken(LocalDateTime.ofInstant(exifDate.toInstant(), ZoneId.of("UTC")));// could use system default alternatively
            }
            entity.setIso(exifDir.getInteger(ExifDirectoryBase.TAG_ISO_EQUIVALENT));
            entity.setFlash(exifDir.getBooleanObject(ExifDirectoryBase.TAG_FLASH));
            entity.setImageWidth(exifDir.getInteger(ExifDirectoryBase.TAG_IMAGE_WIDTH));
            entity.setImageHeight(exifDir.getInteger(ExifDirectoryBase.TAG_IMAGE_HEIGHT));
        }
        entity.setCameraLatitude(null);
        entity.setCameraLongitude(null);
        entity.setCameraAzimuth(null);
        entity.setPositionAccuracy(null);
        entity.setCameraHorizontalFOV(null);
        if (metadataFile != null) { // else no user JSON
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(metadataFile);
            } catch (FileNotFoundException e) {
                log.catching(e);
                log.error("JSON metadata file not found when trying to parse photo metadata. Skipping.");
                return photoDao.save(entity);
            }
            byte[] jsonRaw; // if we haven't returned by now, the file does exist
            try {
                jsonRaw = fis.readAllBytes();
            } catch (IOException e) {
                log.catching(e);
                log.error("JSON metadata file too long (IOException reading). Skipping.");
                return photoDao.save(entity);
            }
            String jsonString = new String(jsonRaw);
            JsonArray jsonObjArr = jsonConverter.fromJson(jsonString, JsonArray.class);
            JsonObject cameraJsonObj = null;
            for(int i=0;i<jsonObjArr.size();i++){
                JsonObject temp = jsonObjArr.get(i).getAsJsonObject();
                if(temp.has("cameraposition")){
                    cameraJsonObj = temp.get("cameraposition").getAsJsonObject();
                }
            }
            if(cameraJsonObj != null) {
                // if a camera position was found (relies on there being at most one, which is reasonable though)
                if (cameraJsonObj.has("latitude")) {
                    entity.setCameraLatitude(cameraJsonObj.get("latitude").getAsDouble());
                }
                if (cameraJsonObj.has("longitude")) {
                    entity.setCameraLongitude(cameraJsonObj.get("longitude").getAsDouble());
                }
                if (cameraJsonObj.has("azimuth")) {
                    entity.setCameraAzimuth(cameraJsonObj.get("azimuth").getAsDouble());
                }
                if (cameraJsonObj.has("accuracy")) {
                    entity.setPositionAccuracy(cameraJsonObj.get("accuracy").getAsDouble());
                }
                if (cameraJsonObj.has("horizontal")) {
                    entity.setCameraHorizontalFOV(cameraJsonObj.get("horizontal").getAsDouble());
                }
            }
        }
        return photoDao.save(entity);
    }

    @Override
    public PhotoEntity updatePhoto(PhotoEntity photo) {
        return photoDao.save(photo);
    }

    @Override
    public void deletePhoto(PhotoEntity photo) {
        photoDao.delete(photo);
    }

    private boolean readFileUpdateHash(File inputFile) {
        byte[] dataArr = new byte[1024];
        try {
            InputStream fis = new FileInputStream(inputFile);
            while (fis.read(dataArr) >= 0) {
                hasher.update(dataArr);
            }
            fis.close();
        } catch (FileNotFoundException e) {
            log.catching(e);
            log.error("File " + inputFile.getAbsolutePath() + " not found.");
            return false;
        } catch (IOException e) {
            log.catching(e);
            log.error("File " + inputFile.getAbsolutePath() + " could not be read.");
            return false;
        }
        return true;
    }

    private String computeBase64(File photoFile, File metadataFile, List<ByteBuffer> extras) {
        hasher.reset();
        for (ByteBuffer buf : extras) {
            hasher.update(buf.array());
        }
        if (!readFileUpdateHash(photoFile)) {
            return null;
        }
        if (metadataFile != null) {
            if (!readFileUpdateHash(metadataFile)) {
                return null;
            }
        }
        byte[] fullHash = hasher.digest();
        byte[] dbHash = Arrays.copyOfRange(fullHash, 7, 16);
        return enc.encodeToString(dbHash);
    }
}