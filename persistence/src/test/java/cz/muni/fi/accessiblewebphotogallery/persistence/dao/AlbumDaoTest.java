package cz.muni.fi.accessiblewebphotogallery.persistence.dao;

import cz.muni.fi.accessiblewebphotogallery.persistence.DatabaseConfig;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AlbumEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static cz.muni.fi.accessiblewebphotogallery.persistence.dao.PhotoDaoTest.createPhoto;
import static cz.muni.fi.accessiblewebphotogallery.persistence.dao.UserDaoTest.setupAsdfUser1;
import static cz.muni.fi.accessiblewebphotogallery.persistence.dao.UserDaoTest.setupFdsaUser2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ContextConfiguration(classes = {DatabaseConfig.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AlbumDaoTest {

    @Autowired
    UserDao userDao;

    @Autowired
    PhotoDao photoDao;

    @Autowired
    AlbumDao albumDao;

    @BeforeAll
    public void init() {
        userDao.deleteAll();
        photoDao.deleteAll();
        assertEquals(0, userDao.count());
        assertEquals(0, photoDao.count());
        UserEntity u1 = userDao.save(setupAsdfUser1());
        UserEntity u2 = userDao.save(setupFdsaUser2());
        assertEquals(2, userDao.count());
        photoDao.save(createPhoto(u1, Instant.now().minusSeconds(20)));
        PhotoEntity secondPhoto = createPhoto(u2, Instant.now().minusSeconds(10));
        secondPhoto.setId("anotherb64id");
        photoDao.save(secondPhoto);
        assertEquals(2, photoDao.count());
    }

    @BeforeEach
    public void clearAlbums() {
        albumDao.deleteAll();
        assertEquals(0, albumDao.count());
    }

    @AfterAll
    public void clearAll() {
        albumDao.deleteAll();
        photoDao.deleteAll();
        userDao.deleteAll();
        assertEquals(0, userDao.count());
        assertEquals(0, photoDao.count());
        assertEquals(0, albumDao.count());
    }

    @Test
    public void findByAlbumOwnerTest() {
        UserEntity user = userDao.findAll().get(0);
        AlbumEntity album = createAlbum(user, "ASDF Album", "thisisab64id");
        album = albumDao.save(album);
        AlbumEntity album2 = createAlbum(userDao.findAll().get(1), "ASDF Album", "anotherb64id");
        album2 = albumDao.save(album2);
        List<AlbumEntity> found = albumDao.findByAlbumOwner(user);
        assertEquals(1, found.size());
        assertEquals(album, found.get(0));
    }

    @Test
    public void findByIdTest() {
        UserEntity user = userDao.findAll().get(0);
        AlbumEntity albumEntity = createAlbum(user, "ASDF Album", "thisisab64iddi64basisiht");
        albumEntity = albumDao.save(albumEntity);
        AlbumEntity albumEntity1 = createAlbum(user, "FDSA Album", "thisisab64iddi64basisilg");
        albumEntity1 = albumDao.save(albumEntity1);
        Optional<AlbumEntity> found = albumDao.findById("thisisab64iddi64basisiht");
        assertTrue(found.isPresent());
        assertEquals(albumEntity, found.get());
    }

    @Test
    public void updateAlbumTest() {
        UserEntity user = userDao.findAll().get(0);
        AlbumEntity albumEntity = createAlbum(user, "ASDF Album", "thisisab64iddi64basisiht");
        albumEntity = albumDao.save(albumEntity);
        AlbumEntity albumEntity1 = createAlbum(user, "FDSA Album", "thisisab64iddi64basisiht");
        albumEntity1 = albumDao.save(albumEntity1);
        assertEquals(albumEntity,albumEntity1);
    }

    public static AlbumEntity createAlbum(UserEntity owner, String name, String base64) {
        AlbumEntity rv = new AlbumEntity();
        rv.setAlbumName(name);
        rv.setAlbumOwner(owner);
        rv.setId(base64);
        return rv;
    }

    @Test
    public void rejectNullIdTest(){
        UserEntity userEntity = userDao.findAll().get(0);
        AlbumEntity albumEntity = createAlbum(userEntity,"ASDFAlbum",null);
        assertThrows(DataAccessException.class,()->{albumDao.save(albumEntity);});
    }


}
