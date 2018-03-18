package cz.muni.fi.accessiblewebphotogallery.persistence.dao;

import cz.muni.fi.accessiblewebphotogallery.persistence.DatabaseConfig;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AlbumEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;

import static cz.muni.fi.accessiblewebphotogallery.persistence.dao.PhotoDaoTest.createPhoto;
import static cz.muni.fi.accessiblewebphotogallery.persistence.dao.UserDaoTest.setupAsdfUser1;
import static cz.muni.fi.accessiblewebphotogallery.persistence.dao.UserDaoTest.setupFdsaUser2;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = {DatabaseConfig.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AlbumDaoTest {

    @Inject
    UserDao userDao;

    @Inject
    PhotoDao photoDao;

    @Inject
    AlbumDao albumDao;

    @BeforeAll
    public void init(){
        userDao.deleteAll();
        photoDao.deleteAll();
        assertEquals(0,userDao.count());
        assertEquals(0,photoDao.count());
        UserEntity u1 = userDao.save(setupAsdfUser1());
        UserEntity u2 = userDao.save(setupFdsaUser2());
        assertEquals(2,userDao.count());
        photoDao.save(createPhoto(u1, Instant.now().minusSeconds(20)));
        PhotoEntity secondPhoto = createPhoto(u2,Instant.now().minusSeconds(10));
        secondPhoto.setBase64Identifier("anotherb64id");
        photoDao.save(secondPhoto);
        assertEquals(2,photoDao.count());
    }

    @BeforeEach
    public void clearAlbums(){
        albumDao.deleteAll();
        assertEquals(0,albumDao.count());
    }

    @AfterAll
    public void clearAll(){
        albumDao.deleteAll();
        photoDao.deleteAll();
        userDao.deleteAll();
        assertEquals(0,userDao.count());
        assertEquals(0,photoDao.count());
        assertEquals(0,albumDao.count());
    }

    @Test
    public void findByAlbumOwnerTest(){
        UserEntity user = userDao.findAll().get(0);
        AlbumEntity album = createAlbum(user,"ASDF Album");
        album = albumDao.save(album);
        AlbumEntity album2 = createAlbum(userDao.findAll().get(1),"ASDF Album");
        album2 = albumDao.save(album2);
        List<AlbumEntity> found = albumDao.findByAlbumOwner(user);
        assertEquals(1,found.size());
        assertEquals(album,found.get(0));
    }

    @Test
    public void findByAlbumNameTest(){
        UserEntity user = userDao.findAll().get(0);
        AlbumEntity album = createAlbum(user,"ASDF Album");
        album = albumDao.save(album);
        AlbumEntity album2 = createAlbum(userDao.findAll().get(1),"FDSA Album");
        album2 = albumDao.save(album2);
        List<AlbumEntity> found = albumDao.findByAlbumName("ASDF Album");
        assertEquals(1,found.size());
        assertEquals(album,found.get(0));
    }

    @Test
    public void findByAlbumNameContainingTest(){
        UserEntity user = userDao.findAll().get(0);
        AlbumEntity album = createAlbum(user,"ASDF Album");
        album = albumDao.save(album);
        AlbumEntity album2 = createAlbum(userDao.findAll().get(1),"FDSA Album");
        album2 = albumDao.save(album2);
        List<AlbumEntity> found = albumDao.findByAlbumNameContainingIgnoreCase("asdf");
        assertEquals(1,found.size());
        assertEquals(album,found.get(0));
    }


    public static AlbumEntity createAlbum(UserEntity owner, String name){
        AlbumEntity rv = new AlbumEntity();
        rv.setAlbumName(name);
        rv.setAlbumOwner(owner);
        return rv;
    }

}