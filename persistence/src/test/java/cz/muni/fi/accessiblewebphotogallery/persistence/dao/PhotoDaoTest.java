package cz.muni.fi.accessiblewebphotogallery.persistence.dao;


import cz.muni.fi.accessiblewebphotogallery.persistence.DatabaseConfig;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static cz.muni.fi.accessiblewebphotogallery.persistence.dao.UserDaoTest.setupAsdfUser1;
import static cz.muni.fi.accessiblewebphotogallery.persistence.dao.UserDaoTest.setupFdsaUser2;
import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = {DatabaseConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
public class PhotoDaoTest {

    @Inject
    PhotoDao photoDao;
    @Inject
    UserDao userDao;

    @BeforeAll
    public void init(){
        userDao.deleteAll();
        assertEquals(0,userDao.count());
        userDao.save(setupAsdfUser1());
        userDao.save(setupFdsaUser2());
        assertEquals(2,userDao.count());
    }

    @BeforeEach
    public void clearPhotos(){
        photoDao.deleteAll();
        assertEquals(0,photoDao.count());
    }

    @AfterAll
    public void clearEverything(){
        photoDao.deleteAll();
        assertEquals(0,photoDao.count());
        userDao.deleteAll();
        assertEquals(0,userDao.count());
    }

    @Test
    public void rejectNullUploaderTest(){
        PhotoEntity photo = createPhoto(null,Instant.now());
        assertThrows(DataAccessException.class,()->{photoDao.save(photo);});
    }

    @Test
    public void rejectNullUploadTimeTest(){
        PhotoEntity photo = createPhoto(userDao.findAll().get(0),null);
        assertThrows(DataAccessException.class,()->{photoDao.save(photo);});
    }

    @Test
    public void rejectNullBase64IdTest(){
        PhotoEntity photo = createPhoto(userDao.findAll().get(0),Instant.now());
        photo.setBase64Id(null);
        assertThrows(DataAccessException.class,()->{photoDao.save(photo);});
    }

    @Test
    public void rejectDuplicitBase64IdTest(){
        PhotoEntity photo = createPhoto(userDao.findAll().get(0),Instant.now());
        PhotoEntity photo2 = createPhoto(userDao.findAll().get(1),Instant.now());
        photoDao.save(photo);
        assertThrows(DataAccessException.class,()->{photoDao.save(photo2);});
    }

    @Test
    public void rejectNullTitleTest(){
        PhotoEntity photo = createPhoto(userDao.findAll().get(0),Instant.now());
        photo.setTitle(null);
        assertThrows(DataAccessException.class,()->{photoDao.save(photo);});
    }

    @Test
    public void rejectNullDescriptionTest(){
        PhotoEntity photo = createPhoto(userDao.findAll().get(0),Instant.now());
        photo.setDescription(null);
        assertThrows(DataAccessException.class,()->{photoDao.save(photo);});
    }

    @Test
    public void rejectTooLongBase64IdTest(){
        PhotoEntity photo = createPhoto(userDao.findAll().get(0),Instant.now());
        photo.setBase64Id("thisbase64identifieristoolong");
        assertThrows(DataAccessException.class,()->{photoDao.save(photo);});
    }

    @Test
    public void doNotRejectNullNullableEXIFInfo(){
        PhotoEntity photo = createPhoto(userDao.findAll().get(0),Instant.now());
        photo.setDatetimeTaken(null);
        photo.setCameraModel(null);
        photoDao.save(photo);
    }

    @Test
    public void findByUploadTimeBetweenTest(){
        UserEntity uploader = userDao.findAll().get(0);// that will do
        Instant time0 = Instant.parse("2018-02-25T17:44:00.000Z");
        Instant time1 = Instant.parse("2018-02-25T17:45:01.349Z");
        Instant time2 = Instant.parse("2018-02-25T17:53:20.001Z");
        PhotoEntity photo = createPhoto(uploader,time1);
        PhotoEntity p2 = createPhoto(uploader, Instant.now());
        p2.setBase64Id("anotherb64id");
        photo = photoDao.save(photo);
        photoDao.save(p2);
        List<PhotoEntity> found = photoDao.findByUploadTimeBetween(time0,time2, PageRequest.of(0,5)).getContent();
        assertEquals(1,found.size());
        assertEquals(photo,found.get(0));
    }

    @Test
    public void findByUploaderTest(){
        UserEntity user = userDao.findAll().get(0);
        PhotoEntity photo = createPhoto(user,Instant.now());
        photo = photoDao.save(photo);
        PhotoEntity photo2 = createPhoto(userDao.findAll().get(1),Instant.now());
        photo2.setBase64Id("anotherb64id");
        photoDao.save(photo2);
        List<PhotoEntity> found = photoDao.findByUploader(user, PageRequest.of(0,3)).getContent();
        assertEquals(1,found.size());
        assertEquals(photo,found.get(0));
    }

    @Test
    public void findByDescriptionContainingIgnoreCaseTest(){
        UserEntity uploader = userDao.findAll().get(0);
        PhotoEntity photo = createPhoto(userDao.findAll().get(0),Instant.now());
        photo.setDescription("Lorem ipsum dolor sit amet, consectetuer adipiscing elit.");
        photo = photoDao.save(photo);
        PhotoEntity photo2 = createPhoto(userDao.findAll().get(1),Instant.now());
        photo2.setBase64Id("anotherb64id");
        photo2.setDescription("Police police police police police police police police.");
        photoDao.save(photo2);
        List<PhotoEntity> found = photoDao.findByDescriptionContainingIgnoreCase("lorem", PageRequest.of(0,3)).getContent();
        assertEquals(1,found.size());
        assertEquals(photo,found.get(0));
    }

    @Test
    public void findByTitleContainingIgnoreCaseTest(){
        PhotoEntity photo = createPhoto(userDao.findAll().get(0),Instant.now());
        photo.setTitle("Lorem ipsum");
        photo = photoDao.save(photo);
        PhotoEntity photo2 = createPhoto(userDao.findAll().get(1),Instant.now());
        photo2.setBase64Id("anotherb64id");
        photo2.setTitle("Photo title");
        photoDao.save(photo2);
        List<PhotoEntity> found = photoDao.findByTitleContainingIgnoreCase("ipsum", PageRequest.of(0,10)).getContent();
        assertEquals(1,found.size());
        assertEquals(photo,found.get(0));
    }

    @Test
    public void findByBase64IdentifierTest(){
        PhotoEntity photo = createPhoto(userDao.findAll().get(0),Instant.now());
        PhotoEntity photo2 = createPhoto(userDao.findAll().get(1),Instant.now());
        photo2.setBase64Id("anotherb64id");
        photo = photoDao.save(photo);
        photoDao.save(photo2);
        Optional<PhotoEntity> found = photoDao.findByBase64Identifier("thisisab64id");
        assertTrue(found.isPresent());
        assertEquals(photo,found.get());
    }

    @Test
    public void findAllOrderedByUploadTimeTest(){
        PhotoEntity photo1 = createPhoto(userDao.findAll().get(0),Instant.now().minusSeconds(30));
        PhotoEntity photo2 = createPhoto(userDao.findAll().get(0),Instant.now().minusSeconds(10));
        photo2.setBase64Id("anotherb64id");
        photo1 = photoDao.save(photo1);
        photo2 = photoDao.save(photo2);
        assertEquals(2,photoDao.count());
        List<PhotoEntity> found = photoDao.findAllByOrderByUploadTimeDesc(PageRequest.of(0,10)).getContent();
        assertTrue(found.get(0).getUploadTime().isAfter(found.get(1).getUploadTime()));
    }

    @Test
    public void updatePhotoTest(){
        PhotoEntity photo = createPhoto(userDao.findAll().get(0),Instant.now());
        photo = photoDao.save(photo);
        String newDesc = "Taken atop the Lomnicky Stit in the High Tatra mountains on 2018-01-10";
        photo.setDescription(newDesc);
        photo = photoDao.save(photo);
        Optional<PhotoEntity> found = photoDao.findByBase64Identifier(photo.getBase64Id());
        assertTrue(found.isPresent());
        assertEquals(newDesc,found.get().getDescription());
    }


    public static PhotoEntity createPhoto(UserEntity uploader, Instant uploadTime){
        PhotoEntity photo = new PhotoEntity();
        photo.setUploader(uploader);
        photo.setUploadTime(uploadTime);
        photo.setBase64Id("thisisab64id");
        photo.setTitle("title");
        photo.setDescription("description");
        photo.setCameraLatitude(53.234);
        photo.setCameraLongitude(33.210);
        photo.setCameraAzimuth(110.33);
        photo.setPositionAccuracy(10.35);
        photo.setCameraFOV(90.5);
        photo.setDatetimeTaken(LocalDateTime.parse("2010-02-24T14:30:22"));
        photo.setCameraModel("N6070c");
        photo.setImageWidth(1920);
        photo.setImageHeight(1080);
        photo.setIso(400);
        photo.setFlash(true);
        photo.setExposureTime(0.01);
        return photo;
    }


}
