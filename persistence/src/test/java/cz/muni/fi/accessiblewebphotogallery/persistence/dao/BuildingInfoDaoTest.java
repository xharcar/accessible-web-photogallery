package cz.muni.fi.accessiblewebphotogallery.persistence.dao;

import cz.muni.fi.accessiblewebphotogallery.persistence.DatabaseConfig;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.BuildingInfo;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;

import static cz.muni.fi.accessiblewebphotogallery.persistence.dao.PhotoDaoTest.createPhoto;
import static cz.muni.fi.accessiblewebphotogallery.persistence.dao.UserDaoTest.setupAsdfUser1;
import static cz.muni.fi.accessiblewebphotogallery.persistence.dao.UserDaoTest.setupFdsaUser2;
import static org.junit.jupiter.api.Assertions.*;


@ContextConfiguration(classes = {DatabaseConfig.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BuildingInfoDaoTest {

    private double EPSILON = 0.0000025;

    @Inject
    UserDao userDao;

    @Inject
    PhotoDao photoDao;

    @Inject
    BuildingInfoDao bidao;

    @BeforeAll
    public void init(){
        Instant time1 = Instant.now();
        userDao.deleteAll();
        photoDao.deleteAll();
        assertEquals(0,userDao.count());
        assertEquals(0,photoDao.count());
        UserEntity user1 = userDao.save(setupAsdfUser1());
        UserEntity user2 = userDao.save(setupFdsaUser2());
        assertEquals(2,userDao.count());
        Instant time2 = Instant.now().plusSeconds(30);
        photoDao.save(createPhoto(user1,time1));
        PhotoEntity photo2 = createPhoto(user2,time2);
        photo2.setBase64Identifier("anotherb64id");
        photoDao.save(photo2);
        assertEquals(2,photoDao.count());
    }

    @BeforeEach
    public void clearBuildingInfoDb(){
        bidao.deleteAll();
        assertEquals(0,bidao.count());
    }

    @AfterAll
    public void clearEverything(){
        bidao.deleteAll();
        assertEquals(0,bidao.count());
        photoDao.deleteAll();
        assertEquals(0,photoDao.count());
        userDao.deleteAll();
        assertEquals(0,userDao.count());
    }

    @Test
    public void doNotRejectNullBuildingNameTest(){
        BuildingInfo info = createBuildingInfo(photoDao.findAll().get(0));
        info.setBuildingName(null);
        bidao.save(info);
    }

    @Test
    public void doNotRejectNullFocusText(){
        BuildingInfo info = createBuildingInfo(photoDao.findAll().get(0));
        info.setFocusText(null);
        bidao.save(info);
    }

    @Test
    public void rejectNullOSMIdTest(){
        BuildingInfo info = createBuildingInfo(photoDao.findAll().get(0));
        info.setOsmId(null);
        assertThrows(DataAccessException.class,()->{bidao.save(info);});
    }

    @Test
    public void findByBuildingNameContainingTest(){
        BuildingInfo info = createBuildingInfo(photoDao.findAll().get(0));
        info = bidao.save(info);
        BuildingInfo info2 = createBuildingInfo(photoDao.findAll().get(1));
        info2.setLatitude(44.902813745);
        info2.setBuildingName("Empire State Building");
        bidao.save(info2);
        List<BuildingInfo> found = bidao.findByBuildingNameContainingIgnoreCase("masaryk");
        assertEquals(1,found.size());
        assertEquals(info,found.get(0));
    }

    @Test
    public void findByPhotoTest(){
        PhotoEntity photo = photoDao.findAll().get(0);
        BuildingInfo info = createBuildingInfo(photo);
        info = bidao.save(info);
        BuildingInfo info2 = createBuildingInfo(photoDao.findAll().get(1));
        info2.setLatitude(44.902813745);
        info2.setBuildingName("Empire State Building");
        bidao.save(info2);
        List<BuildingInfo> found = bidao.findByPhoto(photo);
        assertEquals(1,found.size());
        assertEquals(info,found.get(0));
    }

    @Test
    public void findByOsmIdTest(){
        BuildingInfo info = createBuildingInfo(photoDao.findAll().get(0));
        info = bidao.save(info);
        BuildingInfo info2 = createBuildingInfo(photoDao.findAll().get(1));
        info2.setLatitude(44.902813745);
        info2.setBuildingName("Empire State Building");
        info2.setOsmId(145L);
        info2 = bidao.save(info2);
        List<BuildingInfo> found = bidao.findByOsmId(256L);
        assertEquals(1,found.size());
        assertEquals(info,found.get(0));
    }

    @Test
    public void findByGPSTest(){
        BuildingInfo info = createBuildingInfo(photoDao.findAll().get(0));
        info = bidao.save(info);
        BuildingInfo info2 = createBuildingInfo(photoDao.findAll().get(1));
        info2.setLatitude(44.902813745);
        info2.setBuildingName("Empire State Building");
        info2.setOsmId(145L);
        info2 = bidao.save(info2);
        List<BuildingInfo> found = bidao.findByLatitudeBetweenAndLongitudeBetween(info.getLatitude()-EPSILON,info.getLatitude()+EPSILON,info.getLongitude()-EPSILON, info.getLongitude()+EPSILON);
        assertEquals(1,found.size());
        assertEquals(info,found.get(0));
    }

    public BuildingInfo createBuildingInfo(PhotoEntity photo){
        BuildingInfo rv = new BuildingInfo();
        rv.setPhoto(photo);
        rv.setDistance(153);
        rv.setBuildingName("Fakulta informatiky Masarykovy univerzity");
        rv.setLatitude(33.98327415);
        rv.setLongitude(49.32108745);
        rv.setPhotoMaxX(1024);
        rv.setPhotoMinX(32);
        rv.setOsmId(256L);
        return rv;
    }

}
