package cz.muni.fi.accessiblewebphotogallery.application.facade;

import cz.muni.fi.accessiblewebphotogallery.application.ApplicationConfig;
import cz.muni.fi.accessiblewebphotogallery.application.PhotoGalleryBackendMapper;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.PhotoService;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.facade.facade.PhotoFacade;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ApplicationConfig.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PhotoFacadeTest {

    private PhotoFacade photoFacade;
    @Mock
    private PhotoService photoServiceMock;
    private UserDto defaultUser;
    private Pageable defaultPageRq;
    private Instant timeZero;


    @BeforeAll
    public void init() {
        MockitoAnnotations.initMocks(this);
        photoServiceMock = mock(PhotoService.class);
        photoFacade = new PhotoFacadeImpl(photoServiceMock);
        defaultUser = new UserDto();
        defaultUser.setEmail("ksngmtk@email.org");
        defaultUser.setScreenName("Kusanagi Motoko");
        defaultUser.setAccountState(AccountState.USER);
        defaultUser.setLoginName("ksngmtk");
        defaultUser.setBio("Lorem ipsum dolor sit amet.");
        defaultPageRq = PageRequest.of(0, 10);
        timeZero = Instant.now();
    }

    @Test
    public void findByUploadTimeBetweenTest() {
        PhotoDto p1 = new PhotoDto();
        p1.setUploader(defaultUser);
        p1.setUploadTime(timeZero.minus(Duration.ofDays(20)));
        p1.setTitle("Photo 1");
        p1.setDescription("Description 1");
        p1.setId("thisisab64id");
        PhotoDto p2 = new PhotoDto();
        p2.setUploader(defaultUser);
        p2.setUploadTime(timeZero.minus(Duration.ofDays(2)));
        p2.setTitle("Photo 2");
        p2.setDescription("Description 2");
        p2.setId("anotherb64id");

        List<PhotoDto> photoList = new ArrayList<>();
        photoList.add(p1);
        PageImpl<PhotoDto> expectedPage = new PageImpl<>(photoList, defaultPageRq, 1);

        when(photoServiceMock.findByUploadTimeBetween(timeZero.minus(Duration.ofDays(30)), timeZero, defaultPageRq))
                .thenReturn(new PageImpl<>(photoList.stream().map(this::photoDtoToEntity).collect(Collectors.toList()), defaultPageRq, 1));
        PageImpl<PhotoDto> resultPage = photoFacade.findByUploadTimeBetween(timeZero.minus(Duration.ofDays(30)), timeZero, defaultPageRq);

        assertNotNull(resultPage);
        assertEquals(expectedPage, resultPage);
    }

    @Test
    public void findByUploader() {
        UserDto u2 = new UserDto();
        u2.setEmail("skmtm@email.org");
        u2.setScreenName("Sakamoto Mio");
        u2.setAccountState(AccountState.USER);
        u2.setLoginName("skmtm");
        u2.setBio("Consectetuer adipiscing elit.");
        PhotoDto p1 = new PhotoDto();
        p1.setUploader(defaultUser);
        p1.setUploadTime(timeZero.minus(Duration.ofDays(20)));
        p1.setTitle("Photo 1");
        p1.setDescription("Description 1");
        p1.setId("thisisab64id");
        PhotoDto p2 = new PhotoDto();
        p2.setUploader(u2);
        p2.setUploadTime(timeZero.minus(Duration.ofDays(2)));
        p2.setTitle("Photo 2");
        p2.setDescription("Description 2");
        p2.setId("anotherb64id");

        List<PhotoDto> photoDtoList = new ArrayList<>();
        photoDtoList.add(p1);
        PageImpl<PhotoDto> expectedPage = new PageImpl<>(photoDtoList, defaultPageRq, 1);
        when(photoServiceMock.findByUploader(userDtoToEntity(defaultUser), defaultPageRq))
                .thenReturn(new PageImpl<>(photoDtoList.stream().map(this::photoDtoToEntity).collect(Collectors.toList()), defaultPageRq, 1));
        PageImpl<PhotoDto> resultPage = photoFacade.findByUploader(defaultUser, defaultPageRq);

        assertNotNull(resultPage);
        assertEquals(expectedPage, resultPage);
    }

    @Test
    public void findByDescPartIgnoreCaseTest() {
        PhotoDto p1 = new PhotoDto();
        p1.setUploader(defaultUser);
        p1.setUploadTime(timeZero.minus(Duration.ofDays(20)));
        p1.setTitle("Photo 1");
        p1.setDescription("The Louvre from outside.");
        p1.setId("thisisab64id");
        PhotoDto p2 = new PhotoDto();
        p2.setUploader(defaultUser);
        p2.setUploadTime(timeZero.minus(Duration.ofDays(2)));
        p2.setTitle("Photo 2");
        p2.setDescription("Description 2");
        p2.setId("anotherb64id");

        List<PhotoDto> photoDtoList = new ArrayList<>();
        photoDtoList.add(p1);
        PageImpl<PhotoDto> expectedPage = new PageImpl<>(photoDtoList, defaultPageRq, 1);

        when(photoServiceMock.findByDescriptionApx("louvre", defaultPageRq))
                .thenReturn(new PageImpl<>(photoDtoList.stream().map(this::photoDtoToEntity).collect(Collectors.toList()), defaultPageRq, 1));
        PageImpl<PhotoDto> result = photoFacade.findByDescPartIgnoreCase("louvre", defaultPageRq);

        assertNotNull(result);
        assertEquals(expectedPage, result);
    }

    @Test
    public void findByTitlePartIgnoreCaseTest() {
        PhotoDto p1 = new PhotoDto();
        p1.setUploader(defaultUser);
        p1.setUploadTime(timeZero.minus(Duration.ofDays(20)));
        p1.setTitle("Antwerp Cathedral");
        p1.setDescription("Description 1");
        p1.setId("thisisab64id");
        PhotoDto p2 = new PhotoDto();
        p2.setUploader(defaultUser);
        p2.setUploadTime(timeZero.minus(Duration.ofDays(2)));
        p2.setTitle("Photo 2");
        p2.setDescription("Description 2");
        p2.setId("anotherb64id");

        List<PhotoDto> photoDtoList = new ArrayList<>();
        photoDtoList.add(p1);
        PageImpl<PhotoDto> expectedPage = new PageImpl<>(photoDtoList, defaultPageRq, 1);

        List<PhotoEntity> entityList = photoDtoList.stream().map(this::photoDtoToEntity).collect(Collectors.toList());
        when(photoServiceMock.findByTitleApx("cathedral", defaultPageRq)).thenReturn(new PageImpl<>(entityList, defaultPageRq, 1));
        PageImpl<PhotoDto> result = photoFacade.findByTitlePartIgnoreCase("cathedral", defaultPageRq);

        assertNotNull(result);
        assertEquals(expectedPage.getTotalElements(), result.getTotalElements());
        assertEquals(expectedPage, result);
    }

    @Test
    public void findByBase64Test() {
        PhotoDto p1 = new PhotoDto();
        p1.setUploader(defaultUser);
        p1.setUploadTime(timeZero.minus(Duration.ofDays(20)));
        p1.setTitle("Antwerp Cathedral");
        p1.setDescription("Description 1");
        p1.setId("thisisab64id");
        PhotoDto p2 = new PhotoDto();
        p2.setUploader(defaultUser);
        p2.setUploadTime(timeZero.minus(Duration.ofDays(2)));
        p2.setTitle("Photo 2");
        p2.setDescription("Description 2");
        p2.setId("anotherb64id");

        when(photoServiceMock.findById("thisisab64id")).thenReturn(Optional.of(photoDtoToEntity(p1)));
        Optional<PhotoDto> result = photoFacade.findById("thisisab64id");

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(p1, result.get());
    }

    @Test
    public void findNewestFirstTest() {
        PhotoDto p1 = new PhotoDto();
        p1.setUploader(defaultUser);
        p1.setUploadTime(timeZero.minus(Duration.ofDays(20)));
        p1.setTitle("Antwerp Cathedral");
        p1.setDescription("Description 1");
        p1.setId("thisisab64id");
        PhotoDto p2 = new PhotoDto();
        p2.setUploader(defaultUser);
        p2.setUploadTime(timeZero.minus(Duration.ofDays(2)));
        p2.setTitle("Photo 2");
        p2.setDescription("Description 2");
        p2.setId("anotherb64id");

        List<PhotoDto> photoDtoList = new ArrayList<>();
        photoDtoList.add(p2);
        photoDtoList.add(p1);

        PageImpl<PhotoDto> expectedPage = new PageImpl<>(photoDtoList, defaultPageRq, 2);
        when(photoServiceMock.findNewestFirst(defaultPageRq))
                .thenReturn(new PageImpl<>(photoDtoList.stream().map(this::photoDtoToEntity).collect(Collectors.toList()), defaultPageRq, 2));
        PageImpl<PhotoDto> result = photoFacade.findNewestFirst(defaultPageRq);

        assertNotNull(result);
        assertEquals(expectedPage, result);
    }

    @Test
    public void findMultipleByBase64Test() {
        PhotoDto p1 = new PhotoDto();
        p1.setUploader(defaultUser);
        p1.setUploadTime(timeZero.minus(Duration.ofDays(20)));
        p1.setTitle("Antwerp Cathedral");
        p1.setDescription("Description 1");
        p1.setId("thisisab64id");
        PhotoDto p2 = new PhotoDto();
        p2.setUploader(defaultUser);
        p2.setUploadTime(timeZero.minus(Duration.ofDays(2)));
        p2.setTitle("Photo 2");
        p2.setDescription("Description 2");
        p2.setId("anotherb64id");
        PhotoDto p3 = new PhotoDto();
        p3.setUploader(defaultUser);
        p3.setUploadTime(timeZero.minus(Duration.ofDays(10)));
        p3.setTitle("Title 3");
        p3.setDescription("Description 3");
        p3.setId("b64idnumber3");

        when(photoServiceMock.findById("thisisab64id")).thenReturn(Optional.of(photoDtoToEntity(p1)));
        when(photoServiceMock.findById("anotherb64id")).thenReturn(Optional.of(photoDtoToEntity(p2)));
        List<String> idsToSearch = new ArrayList<>();
        idsToSearch.add(p1.getId());
        idsToSearch.add(p2.getId());
        PageImpl<PhotoDto> result = photoFacade.findMultipleByBase64(idsToSearch, defaultPageRq);

        assertNotNull(result);
        assertTrue(result.getContent().contains(p1));
        assertTrue(result.getContent().contains(p2));
        assertFalse(result.getContent().contains(p3));
    }

    @Test
    public void updatePhotoTest() {
        PhotoDto p1 = new PhotoDto();
        p1.setUploader(defaultUser);
        p1.setUploadTime(timeZero.minus(Duration.ofDays(20)));
        p1.setTitle("Antwerp Cathedral");
        p1.setDescription("Description 1");
        p1.setId("thisisab64id");
        PhotoDto p2 = new PhotoDto();
        p2.setUploader(defaultUser);
        p2.setUploadTime(timeZero.minus(Duration.ofDays(20)));
        p2.setTitle("Photo 2");
        p2.setDescription("Description 2");
        p2.setId("thisisab64id");

        PhotoEntity p2e = photoDtoToEntity(p2);
        when(photoServiceMock.updatePhoto(p2e)).thenReturn(p2e);
        photoFacade.updatePhoto(p2);
        assertEquals(p1, p2);
    }


    PhotoEntity photoDtoToEntity(PhotoDto dto) {
        return PhotoGalleryBackendMapper.photoDtoToEntity(dto);
    }

    private UserEntity userDtoToEntity(UserDto dto) {
        return PhotoGalleryBackendMapper.userDtoToEntity(dto);
    }
}
