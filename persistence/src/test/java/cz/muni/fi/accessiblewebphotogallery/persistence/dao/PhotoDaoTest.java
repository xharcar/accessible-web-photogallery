package cz.muni.fi.accessiblewebphotogallery.persistence.dao;


import cz.muni.fi.accessiblewebphotogallery.persistence.DatabaseConfig;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static cz.muni.fi.accessiblewebphotogallery.persistence.dao.UserDaoTest.setupAsdfUser1;
import static cz.muni.fi.accessiblewebphotogallery.persistence.dao.UserDaoTest.setupFdsaUser2;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    public void findByUploadTimeBetweenTest(){
        PhotoEntity photo = new PhotoEntity();
        photo.setUploader(userDao.findAll().get(0)); // that will do
        Instant time0 = Instant.parse("2018-02-25T17:44:00.000Z");
        Instant time1 = Instant.parse("2018-02-25T17:45:01.349Z");
        Instant time2 = Instant.parse("2018-02-25T17:53:20.001Z");
        photo.setUploadTime(time1);
        photo.setBase64Identifier("thisisab64id");
        photo.setTitle("title");
        photo.setDescription("description");
        photo.setCameraLatitude(53.234);
        photo.setCameraLongitude(33.210);
        photo.setCameraAzimuth(110.33);
        photo.setPositionAccuracy(10.35);
        photo.setCameraHorizontalFOV(90.5);
        photo.setDatetimeTaken(LocalDateTime.parse("2017-02-24T14:30:22"));
        photo.setCameraModel("N6070c");
        photo.setImageWidth(1920);
        photo.setImageHeight(1080);
        photo.setIso(400);
        photo.setFlash(true);
        photo.setExposureTime(0.01);
        photoDao.save(photo);
        List<PhotoEntity> found = photoDao.findByUploadTimeBetween(time0,time2);
        assertEquals(1,found.size());
        assertEquals(photo,found.get(0));
    }

    // more tests TBD





}
