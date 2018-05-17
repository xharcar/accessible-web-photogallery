package cz.muni.fi.accessiblewebphotogallery.application.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cz.muni.fi.accessiblewebphotogallery.application.ApplicationConfig;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.BuildingInfoService;
import cz.muni.fi.accessiblewebphotogallery.persistence.dao.BuildingInfoDao;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.BuildingInfo;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ApplicationConfig.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BuildingInfoServiceTest {


    private BuildingInfoService infoService;
    @Mock
    private BuildingInfoDao infoDaoMock;

    private byte[] dummyhash1 = new byte[]{0x32, (byte) 0xFC, 0x5A};
    private byte[] dummysalt1 = new byte[]{0x3A, (byte) 0xB8, (byte) 0xE1};
    private UserEntity defaultUser;
    private PhotoEntity p1;
    private PhotoEntity p2;
    private double EPSILON = 0.0000025;
    private Gson jsonConverter;

    @BeforeAll
    public void init() {
        MockitoAnnotations.initMocks(this);
        infoDaoMock = mock(BuildingInfoDao.class);
        infoService = new BuildingInfoServiceImpl(infoDaoMock);
        defaultUser = new UserEntity();
        defaultUser.setEmail("abdc@email.org");
        defaultUser.setScreenName("DKXC");
        defaultUser.setAccountState(AccountState.USER);
        defaultUser.setLoginName("xdck");
        defaultUser.setBio("Lorem ipsum dolor sit amet.");
        defaultUser.setPasswordHash(dummyhash1);
        defaultUser.setPasswordSalt(dummysalt1);
        Instant timeZero = Instant.now();
        p1 = new PhotoEntity();
        p1.setUploader(defaultUser);
        p1.setUploadTime(timeZero.minus(Duration.ofDays(20)));
        p1.setTitle("Photo 1");
        p1.setDescription("Description 1");
        p1.setId("thisisab64id");
        p2 = new PhotoEntity();
        p2.setUploader(defaultUser);
        p2.setUploadTime(timeZero.minusSeconds(600));
        p2.setTitle("Photo 2");
        p2.setDescription("Description 2");
        p2.setId("anotherb64id");
        jsonConverter = new Gson();
    }

    @Test
    public void findAllTest() {
        BuildingInfo info1 = new BuildingInfo();
        info1.setOsmId(1L);
        info1.setPhoto(p1);
        info1.setLeftBoundInPhoto(0);
        info1.setPhotoMaxX(460);
        info1.setDistance(125);
        info1.setLatitude(48.35);
        info1.setLongitude(12.54);
        BuildingInfo info2 = new BuildingInfo();
        info2.setOsmId(2L);
        info2.setPhoto(p2);
        info2.setLeftBoundInPhoto(134);
        info2.setPhotoMaxX(1045);
        info2.setDistance(210);
        info2.setLatitude(32.81);
        info2.setLongitude(16.97);

        List<BuildingInfo> infoList = new ArrayList<>();
        infoList.add(info1);
        infoList.add(info2);
        when(infoDaoMock.findAll()).thenReturn(infoList);

        List<BuildingInfo> result = infoService.findAll();
        assertNotNull(result);
        assertEquals(infoList, result);
    }

    @Test
    public void findByPhotoTest() {
        BuildingInfo info1 = new BuildingInfo();
        info1.setOsmId(1L);
        info1.setPhoto(p1);
        info1.setLeftBoundInPhoto(0);
        info1.setPhotoMaxX(460);
        info1.setDistance(125);
        info1.setLatitude(48.35);
        info1.setLongitude(12.54);
        BuildingInfo info2 = new BuildingInfo();
        info2.setOsmId(2L);
        info2.setPhoto(p2);
        info2.setLeftBoundInPhoto(134);
        info2.setPhotoMaxX(1045);
        info2.setDistance(210);
        info2.setLatitude(32.81);
        info2.setLongitude(16.97);

        List<BuildingInfo> infoList = new ArrayList<>();
        infoList.add(info1);
        when(infoDaoMock.findByPhoto(p1)).thenReturn(infoList);

        List<BuildingInfo> result = infoService.findByPhoto(p1);
        assertNotNull(result);
        assertEquals(infoList, result);
    }

    @Test
    public void findByNameNoCaseTest() {
        BuildingInfo info1 = new BuildingInfo();
        info1.setOsmId(1L);
        info1.setPhoto(p1);
        info1.setLeftBoundInPhoto(0);
        info1.setPhotoMaxX(460);
        info1.setDistance(125);
        info1.setLatitude(48.35);
        info1.setLongitude(12.54);
        info1.setBuildingName("Random building in the street");
        BuildingInfo info2 = new BuildingInfo();
        info2.setOsmId(2L);
        info2.setPhoto(p2);
        info2.setLeftBoundInPhoto(134);
        info2.setPhotoMaxX(1045);
        info2.setDistance(210);
        info2.setLatitude(32.81);
        info2.setLongitude(16.97);

        List<BuildingInfo> infoList = new ArrayList<>();
        infoList.add(info1);
        when(infoDaoMock.findByBuildingNameContainingIgnoreCase("random")).thenReturn(infoList);

        List<BuildingInfo> result = infoService.findByBuildingNameApx("random");
        assertNotNull(result);
        assertEquals(infoList, result);
    }

    @Test
    public void findByOsmIdTest() {
        BuildingInfo info1 = new BuildingInfo();
        info1.setOsmId(1L);
        info1.setPhoto(p1);
        info1.setLeftBoundInPhoto(0);
        info1.setPhotoMaxX(460);
        info1.setDistance(125);
        info1.setLatitude(48.35);
        info1.setLongitude(12.54);
        BuildingInfo info2 = new BuildingInfo();
        info2.setOsmId(2L);
        info2.setPhoto(p2);
        info2.setLeftBoundInPhoto(134);
        info2.setPhotoMaxX(1045);
        info2.setDistance(210);
        info2.setLatitude(32.81);
        info2.setLongitude(16.97);

        List<BuildingInfo> infoList = new ArrayList<>();
        infoList.add(info1);
        when(infoDaoMock.findByOsmId(1L)).thenReturn(infoList);

        List<BuildingInfo> result = infoService.findByOsmId(1L);
        assertNotNull(result);
        assertEquals(infoList, result);
    }

    @Test
    public void findByGPSTest() {
        BuildingInfo info1 = new BuildingInfo();
        info1.setOsmId(1L);
        info1.setPhoto(p1);
        info1.setLeftBoundInPhoto(0);
        info1.setPhotoMaxX(460);
        info1.setDistance(125);
        info1.setLatitude(48.35);
        info1.setLongitude(12.54);
        BuildingInfo info2 = new BuildingInfo();
        info2.setOsmId(2L);
        info2.setPhoto(p2);
        info2.setLeftBoundInPhoto(134);
        info2.setPhotoMaxX(1045);
        info2.setDistance(210);
        info2.setLatitude(32.81);
        info2.setLongitude(16.97);

        List<BuildingInfo> infoList = new ArrayList<>();
        infoList.add(info1);
        when(infoDaoMock.findByLatitudeBetweenAndLongitudeBetween(info1.getLatitude() - EPSILON,
                info1.getLatitude() + EPSILON, info1.getLongitude() - EPSILON,
                info1.getLongitude() + EPSILON)).thenReturn(infoList);

        List<BuildingInfo> result = infoService.findByGPSPosition(info1.getLatitude(), info1.getLongitude());
        assertNotNull(result);
        assertEquals(infoList, result);
    }

    @Test
    public void saveInfosTest() {
        File originalMetadataFile = new File("../sample_data/IMG_20171020_163217");
        File nominatimFile = new File("../sample_data/IMG_20171020_163217_nominatim.json");

        InputStream origMDFIS = null;
        try {
            origMDFIS = new FileInputStream(originalMetadataFile);
        } catch (FileNotFoundException e) {
            fail("original metadata file not found");
        }
        byte[] jsonRaw = null;
        try {
            jsonRaw = origMDFIS.readAllBytes();
        } catch (IOException e) {
            fail("JSON metadata file too long (IOException reading). Skipping.");
        }
        String jsonString = new String(jsonRaw);
        JsonArray jsonObjArr = jsonConverter.fromJson(jsonString, JsonArray.class);
        JsonObject cameraJsonObj = null;
        JsonObject originalPhotoJson = null;
        for (int i = 0; i < jsonObjArr.size(); i++) {
            JsonObject temp = jsonObjArr.get(i).getAsJsonObject();
            if (temp.has("cameraposition")) {
                cameraJsonObj = temp.get("cameraposition").getAsJsonObject();
            } else {
                originalPhotoJson = temp;
                // the test file only has 1 building object and the camera position object
            }
        }
        if (cameraJsonObj == null) {
            fail("couldn't retrieve camera position data");
        }
        InputStream nominatimFIS = null;
        try {
            nominatimFIS = new FileInputStream(nominatimFile);
        } catch (FileNotFoundException e) {
            fail("Nominatim metadata file not found");
        }
        byte[] nominatimRaw = null;
        try {
            nominatimRaw = nominatimFIS.readAllBytes();
        } catch (IOException e) {
            fail("JSON metadata file too long (IOException reading). Skipping.");
        }
        String nominatimString = new String(nominatimRaw);
        JsonObject nominatimJson = jsonConverter.fromJson(nominatimString, JsonObject.class);
        Map<JsonObject, JsonObject> jsonMap = new HashMap<>();
        jsonMap.put(originalPhotoJson, nominatimJson);
        when(infoDaoMock.save(any(BuildingInfo.class))).then(new Answer<BuildingInfo>() {
            @Override
            public BuildingInfo answer(InvocationOnMock invocation) throws Throwable {
                BuildingInfo info = invocation.getArgument(0);
                info.setId("thisisab64id16ch"); // for this test, there will be only one
                return info;
            }
        });
        p1.setCameraFOV(60.0);
        p1.setImageWidth(2592);
        List<BuildingInfo> infoList = infoService.registerBuildings(jsonMap, cameraJsonObj, p1);
        assertNotNull(infoList);
        assertEquals(1, infoList.size());
        assertNotNull(infoList.get(0).getId());
        assertEquals(2973995322L, infoList.get(0).getOsmId().longValue());
    }

    @Test
    public void updateInfoTest() {
        BuildingInfo info1 = new BuildingInfo();
        info1.setOsmId(1L);
        info1.setPhoto(p1);
        info1.setLeftBoundInPhoto(0);
        info1.setPhotoMaxX(460);
        info1.setDistance(125);
        info1.setLatitude(48.35);
        info1.setLongitude(12.54);
        BuildingInfo info2 = new BuildingInfo();
        info2.setOsmId(1L);
        info2.setPhoto(p1);
        info2.setLeftBoundInPhoto(134);
        info2.setPhotoMaxX(1045);
        info2.setDistance(210);
        info2.setLatitude(48.35);
        info2.setLongitude(12.54);

        when(infoDaoMock.save(info2)).thenReturn(info2);
        infoService.updateBuildingInfo(info2);
        assertEquals(info1, info2);
    }
}
