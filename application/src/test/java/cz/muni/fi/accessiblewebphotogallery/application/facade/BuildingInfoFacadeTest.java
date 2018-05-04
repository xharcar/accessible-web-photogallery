package cz.muni.fi.accessiblewebphotogallery.application.facade;

import cz.muni.fi.accessiblewebphotogallery.iface.ApplicationConfig;
import cz.muni.fi.accessiblewebphotogallery.application.PhotoGalleryBackendMapper;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.BuildingInfoService;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.BuildingInfoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.BuildingInfoFacade;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.BuildingInfo;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ApplicationConfig.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BuildingInfoFacadeTest {

    private BuildingInfoFacade infoFacade;
    @Mock
    private BuildingInfoService infoServiceMock;
    private UserDto defaultUser;
    private PhotoDto p1;
    private PhotoDto p2;


    @BeforeAll
    public void init(){
        infoServiceMock = mock(BuildingInfoService.class);
        infoFacade = new BuildingInfoFacadeImpl(infoServiceMock);
        defaultUser = new UserDto();
        defaultUser.setEmail("ksngmtk@email.org");
        defaultUser.setScreenName("Kusanagi Motoko");
        defaultUser.setAccountState(AccountState.USER);
        defaultUser.setLoginName("ksngmtk");
        defaultUser.setBio("Lorem ipsum dolor sit amet.");
        p1 = new PhotoDto();
        p1.setUploader(defaultUser);
        p1.setUploadTime(Instant.now().minus(Duration.ofDays(20)));
        p1.setTitle("Photo 1");
        p1.setDescription("Description 1");
        p1.setBase64Id("thisisab64id");
        p2 = new PhotoDto();
        p2.setUploader(defaultUser);
        p2.setUploadTime(Instant.now().minusSeconds(600));
        p2.setTitle("Photo 2");
        p2.setDescription("Description 2");
        p2.setBase64Id("anotherb64id");
    }

    @Test
    public void findAllTest(){
        BuildingInfoDto info1 = new BuildingInfoDto();
        info1.setOsmId(1L);
        info1.setPhoto(p1);
        info1.setPhotoMinX(0);
        info1.setPhotoMaxX(460);
        info1.setDistance(125);
        info1.setLatitude(48.35);
        info1.setLongitude(12.54);
        BuildingInfoDto info2 = new BuildingInfoDto();
        info2.setOsmId(2L);
        info2.setPhoto(p2);
        info2.setPhotoMinX(134);
        info2.setPhotoMaxX(1045);
        info2.setDistance(210);
        info2.setLatitude(32.81);
        info2.setLongitude(16.97);

        List<BuildingInfoDto> infoList = new ArrayList<>();
        infoList.add(info1);
        infoList.add(info2);
        when(infoServiceMock.findAll()).thenReturn(infoList.stream().map(this::buildingInfoDtoToEntity).collect(Collectors.toList()));

        List<BuildingInfoDto> result = infoFacade.findAll();
        assertNotNull(result);
        assertEquals(infoList, result);
    }

    @Test
    public void findByPhotoTest(){
        BuildingInfoDto info1 = new BuildingInfoDto();
        info1.setOsmId(1L);
        info1.setPhoto(p1);
        info1.setPhotoMinX(0);
        info1.setPhotoMaxX(460);
        info1.setDistance(125);
        info1.setLatitude(48.35);
        info1.setLongitude(12.54);
        BuildingInfoDto info2 = new BuildingInfoDto();
        info2.setOsmId(2L);
        info2.setPhoto(p2);
        info2.setPhotoMinX(134);
        info2.setPhotoMaxX(1045);
        info2.setDistance(210);
        info2.setLatitude(32.81);
        info2.setLongitude(16.97);

        List<BuildingInfoDto> infoList = new ArrayList<>();
        infoList.add(info1);
        when(infoServiceMock.findByPhoto(photoDtoToEntity(p1))).thenReturn(infoList.stream().map(this::buildingInfoDtoToEntity).collect(Collectors.toList()));

        List<BuildingInfoDto> result = infoFacade.findByPhoto(p1);
        assertNotNull(result);
        assertEquals(infoList, result);
    }

    @Test
    public void findByBuildingName(){
        BuildingInfoDto info1 = new BuildingInfoDto();
        info1.setOsmId(1L);
        info1.setPhoto(p1);
        info1.setPhotoMinX(0);
        info1.setPhotoMaxX(460);
        info1.setDistance(125);
        info1.setLatitude(48.35);
        info1.setLongitude(12.54);
        info1.setBuildingName("Empire State Building");
        BuildingInfoDto info2 = new BuildingInfoDto();
        info2.setOsmId(2L);
        info2.setPhoto(p2);
        info2.setPhotoMinX(134);
        info2.setPhotoMaxX(1045);
        info2.setDistance(210);
        info2.setLatitude(32.81);
        info2.setLongitude(16.97);

        List<BuildingInfoDto> infoList = new ArrayList<>();
        infoList.add(info1);
        when(infoServiceMock.findByBuildingNameApx("empire")).thenReturn(infoList.stream().map(this::buildingInfoDtoToEntity).collect(Collectors.toList()));

        List<BuildingInfoDto> result = infoFacade.findByBuildingNameApx("empire");
        assertNotNull(result);
        assertEquals(infoList, result);

    }

    @Test
    public void findByOsmIdTest(){
        BuildingInfoDto info1 = new BuildingInfoDto();
        info1.setOsmId(1L);
        info1.setPhoto(p1);
        info1.setPhotoMinX(0);
        info1.setPhotoMaxX(460);
        info1.setDistance(125);
        info1.setLatitude(48.35);
        info1.setLongitude(12.54);
        BuildingInfoDto info2 = new BuildingInfoDto();
        info2.setOsmId(2L);
        info2.setPhoto(p2);
        info2.setPhotoMinX(134);
        info2.setPhotoMaxX(1045);
        info2.setDistance(210);
        info2.setLatitude(32.81);
        info2.setLongitude(16.97);

        List<BuildingInfoDto> infoList = new ArrayList<>();
        infoList.add(info1);
        when(infoServiceMock.findByOsmId(1L)).thenReturn(infoList.stream().map(this::buildingInfoDtoToEntity).collect(Collectors.toList()));

        List<BuildingInfoDto> result = infoFacade.findByOsmId(1L);
        assertNotNull(result);
        assertEquals(infoList, result);
    }

    @Test
    public void findByGPSTest(){
        BuildingInfoDto info1 = new BuildingInfoDto();
        info1.setOsmId(1L);
        info1.setPhoto(p1);
        info1.setPhotoMinX(0);
        info1.setPhotoMaxX(460);
        info1.setDistance(125);
        info1.setLatitude(48.35);
        info1.setLongitude(12.54);
        BuildingInfoDto info2 = new BuildingInfoDto();
        info2.setOsmId(2L);
        info2.setPhoto(p2);
        info2.setPhotoMinX(134);
        info2.setPhotoMaxX(1045);
        info2.setDistance(210);
        info2.setLatitude(32.81);
        info2.setLongitude(16.97);

        List<BuildingInfoDto> infoList = new ArrayList<>();
        infoList.add(info1);
        when(infoServiceMock.findByGPSPosition(48.35,12.54)).thenReturn(infoList.stream().map(this::buildingInfoDtoToEntity).collect(Collectors.toList()));

        List<BuildingInfoDto> result = infoFacade.findByGPSPosition(48.35,12.54);
        assertNotNull(result);
        assertEquals(infoList, result);
    }

    @Test
    public void updateBuildingInfoTest(){
        BuildingInfoDto info1 = new BuildingInfoDto();
        info1.setOsmId(1L);
        info1.setPhoto(p1);
        info1.setPhotoMinX(0);
        info1.setPhotoMaxX(460);
        info1.setDistance(125);
        info1.setLatitude(48.35);
        info1.setLongitude(12.54);
        BuildingInfoDto info2 = new BuildingInfoDto();
        info2.setOsmId(1L);
        info2.setPhoto(p1);
        info2.setPhotoMinX(134);
        info2.setPhotoMaxX(1045);
        info2.setDistance(125);
        info2.setLatitude(48.35);
        info2.setLongitude(12.54);

        when(infoServiceMock.updateBuildingInfo(buildingInfoDtoToEntity(info2))).thenReturn(buildingInfoDtoToEntity(info2));
        infoFacade.updateBuildingInfo(info2);
        assertEquals(info1,info2);
    }


    private BuildingInfo buildingInfoDtoToEntity(BuildingInfoDto dto){return PhotoGalleryBackendMapper.buildingInfoDtoToEntity(dto);}
    private PhotoEntity photoDtoToEntity(PhotoDto dto){return PhotoGalleryBackendMapper.photoDtoToEntity(dto);}
}