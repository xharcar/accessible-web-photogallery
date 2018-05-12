package cz.muni.fi.accessiblewebphotogallery.application.service;


import cz.muni.fi.accessiblewebphotogallery.iface.ApplicationConfig;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.PhotoService;
import cz.muni.fi.accessiblewebphotogallery.persistence.dao.PhotoDao;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ApplicationConfig.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PhotoServiceTest {

    @Mock
    private PhotoDao photoDaoMock;
    private PhotoService photoService;
    private Pageable defaultPageRq;
    private UserEntity defaultUser;
    private byte[] dummyhash1 = new byte[]{0x32, (byte) 0xFC,0x5A};
    private byte[] dummysalt1 = new byte[]{0x3A, (byte) 0xB8, (byte) 0xE1};
    private Instant timeZero;
    @Inject
    ApplicationConfig applicationConfig;


    @BeforeAll
    public void init(){
        MockitoAnnotations.initMocks(this);
        photoDaoMock = mock(PhotoDao.class);
        photoService = new PhotoServiceImpl(photoDaoMock,applicationConfig);
        defaultPageRq = PageRequest.of(0,10);
        defaultUser = new UserEntity();
        defaultUser.setEmail("abdc@email.org");
        defaultUser.setScreenName("DKXC");
        defaultUser.setAccountState(AccountState.USER);
        defaultUser.setLoginName("xdck");
        defaultUser.setBio("Lorem ipsum dolor sit amet.");
        defaultUser.setPasswordHash(dummyhash1);
        defaultUser.setPasswordSalt(dummysalt1);
        timeZero = Instant.now();
    }

    @Test
    public void findByUploadTimeBetweenTest(){
        PhotoEntity p1 = new PhotoEntity();
        p1.setUploader(defaultUser);
        p1.setUploadTime(timeZero.minus(Duration.ofDays(20)));
        p1.setTitle("Photo 1");
        p1.setDescription("Description 1");
        p1.setBase64Identifier("thisisab64id");
        PhotoEntity p2 = new PhotoEntity();
        p2.setUploader(defaultUser);
        p2.setUploadTime(timeZero.minusSeconds(600));
        p2.setTitle("Photo 2");
        p2.setDescription("Description 2");
        p2.setBase64Identifier("anotherb64id");

        List<PhotoEntity> photoList = new ArrayList<>();
        photoList.add(p1);
        PageImpl<PhotoEntity> expectedPage = new PageImpl<>(photoList,defaultPageRq,1);

        PageImpl<PhotoEntity> returnPage = new PageImpl<>(photoList,defaultPageRq,1);
        when(photoDaoMock.findByUploadTimeBetween(timeZero.minus(Duration.ofDays(30)),timeZero.minus(Duration.ofDays(16)),defaultPageRq))
                .thenReturn(returnPage);
        PageImpl<PhotoEntity> result = photoService.findByUploadTimeBetween(timeZero.minus(Duration.ofDays(30)),timeZero.minus(Duration.ofDays(16)),defaultPageRq);
        assertNotNull(result);
        assertEquals(expectedPage,result);
    }

    @Test
    public void findByUploaderTest(){
        PhotoEntity p1 = new PhotoEntity();
        p1.setUploader(defaultUser);
        p1.setUploadTime(timeZero.minus(Duration.ofDays(20)));
        p1.setTitle("Photo 1");
        p1.setDescription("Description 1");
        p1.setBase64Identifier("thisisab64id");

        UserEntity anotherUser = new UserEntity();
        anotherUser.setPasswordHash(dummysalt1);
        anotherUser.setPasswordSalt(dummyhash1);
        anotherUser.setEmail("mymail@email.org");
        anotherUser.setAccountState(AccountState.USER);
        anotherUser.setLoginName("randomuser");
        anotherUser.setScreenName("Random User");
        anotherUser.setBio("Nothing to see here.");

        PhotoEntity p2 = new PhotoEntity();
        p2.setUploader(anotherUser);
        p2.setUploadTime(timeZero.minusSeconds(600));
        p2.setTitle("Photo 2");
        p2.setDescription("Description 2");
        p2.setBase64Identifier("anotherb64id");

        List<PhotoEntity> photoList = new ArrayList<>();
        photoList.add(p1);
        PageImpl<PhotoEntity> expectedPage = new PageImpl<>(photoList,defaultPageRq,1);

        PageImpl<PhotoEntity> returnPage = new PageImpl<>(photoList,defaultPageRq,1);
        when(photoDaoMock.findByUploader(defaultUser,defaultPageRq)).thenReturn(returnPage);
        PageImpl<PhotoEntity> result = photoService.findByUploader(defaultUser,defaultPageRq);

        assertNotNull(result);
        assertEquals(expectedPage,result);
    }

    @Test
    public void findByDescPartIgnoreCaseTest(){
        PhotoEntity p1 = new PhotoEntity();
        p1.setUploader(defaultUser);
        p1.setUploadTime(timeZero.minus(Duration.ofDays(20)));
        p1.setTitle("Photo 1");
        p1.setDescription("Taken while on my recent trip to Antwerp, Belgium.");
        p1.setBase64Identifier("thisisab64id");

        PhotoEntity p2 = new PhotoEntity();
        p2.setUploader(defaultUser);
        p2.setUploadTime(timeZero.minusSeconds(600));
        p2.setTitle("Photo 2");
        p2.setDescription("Description 2");
        p2.setBase64Identifier("anotherb64id");

        List<PhotoEntity> photoList = new ArrayList<>();
        photoList.add(p1);
        PageImpl<PhotoEntity> expectedPage = new PageImpl<>(photoList,defaultPageRq,1);

        PageImpl<PhotoEntity> returnPage = new PageImpl<>(photoList,defaultPageRq,1);
        when(photoDaoMock.findByDescriptionContainingIgnoreCase("antwerp",defaultPageRq)).thenReturn(returnPage);
        PageImpl<PhotoEntity> result = photoService.findByDescriptionApx("antwerp",defaultPageRq);

        assertNotNull(result);
        assertEquals(expectedPage,result);
    }

    @Test
    public void findByTitlePartIgnoreCaseTest() {
        PhotoEntity p1 = new PhotoEntity();
        p1.setUploader(defaultUser);
        p1.setUploadTime(timeZero.minus(Duration.ofDays(20)));
        p1.setTitle("Louvre");
        p1.setDescription("Description 1");
        p1.setBase64Identifier("thisisab64id");

        PhotoEntity p2 = new PhotoEntity();
        p2.setUploader(defaultUser);
        p2.setUploadTime(timeZero.minusSeconds(600));
        p2.setTitle("Photo 2");
        p2.setDescription("Description 2");
        p2.setBase64Identifier("anotherb64id");

        List<PhotoEntity> photoList = new ArrayList<>();
        photoList.add(p1);
        PageImpl<PhotoEntity> expectedPage = new PageImpl<>(photoList,defaultPageRq,1);

        PageImpl<PhotoEntity> returnPage = new PageImpl<>(photoList, defaultPageRq, 1);
        when(photoDaoMock.findByTitleContainingIgnoreCase("louvre", defaultPageRq)).thenReturn(returnPage);
        PageImpl<PhotoEntity> result = photoService.findByTitleApx("louvre", defaultPageRq);

        assertNotNull(result);
        assertEquals(expectedPage, result);
    }

    @Test
    public void findByBase64Test(){
        PhotoEntity p1 = new PhotoEntity();
        p1.setUploader(defaultUser);
        p1.setUploadTime(timeZero.minus(Duration.ofDays(20)));
        p1.setTitle("Photo 1");
        p1.setDescription("Description 1");
        p1.setBase64Identifier("thisisab64id");

        PhotoEntity p2 = new PhotoEntity();
        p2.setUploader(defaultUser);
        p2.setUploadTime(timeZero.minusSeconds(600));
        p2.setTitle("Photo 2");
        p2.setDescription("Description 2");
        p2.setBase64Identifier("anotherb64id");

        List<PhotoEntity> photoList = new ArrayList<>();
        photoList.add(p1);
        
        when(photoDaoMock.findByBase64Identifier("thisisab64id")).thenReturn(Optional.of(p1));
        Optional<PhotoEntity> result = photoService.findByBase64Id("thisisab64id");

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(p1, result.get());
    }

    @Test
    public void findNewestFirstTest(){
        PhotoEntity p1 = new PhotoEntity();
        p1.setUploader(defaultUser);
        p1.setUploadTime(timeZero.minus(Duration.ofDays(20)));
        p1.setTitle("Photo 1");
        p1.setDescription("Description 1");
        p1.setBase64Identifier("thisisab64id");

        PhotoEntity p2 = new PhotoEntity();
        p2.setUploader(defaultUser);
        p2.setUploadTime(timeZero.minusSeconds(600));
        p2.setTitle("Photo 2");
        p2.setDescription("Description 2");
        p2.setBase64Identifier("anotherb64id");

        List<PhotoEntity> photoList = new ArrayList<>();
        photoList.add(p2);
        photoList.add(p1);
        PageImpl<PhotoEntity> expectedPage = new PageImpl<>(photoList,defaultPageRq,1);

        PageImpl<PhotoEntity> returnPage = new PageImpl<>(photoList, defaultPageRq, 1);
        when(photoDaoMock.findAllByOrderByUploadTimeDesc(defaultPageRq)).thenReturn(returnPage);
        PageImpl<PhotoEntity> result = photoService.findNewestFirst(defaultPageRq);

        assertNotNull(result);
        assertEquals(expectedPage, result);
        for(int i=0;i<expectedPage.getTotalElements()-1;i++){
            assertEquals(expectedPage.getContent().get(i),result.getContent().get(i));
        } // same order
    }

    @Test
    public void registerPhotoTest(){
        PhotoEntity p1 = new PhotoEntity();
        p1.setUploader(defaultUser);
        p1.setUploadTime(timeZero.minus(Duration.ofDays(20)));
        p1.setTitle("Photo 1");
        p1.setDescription("Description 1");
        p1.setBase64Identifier("thisisab64id");

        when(photoDaoMock.save(p1)).then(new Answer<PhotoEntity>() {
            @Override
            public PhotoEntity answer(InvocationOnMock invocation) throws Throwable {
                PhotoEntity p = (PhotoEntity) invocation.getArgument(0);
                p.setId(1L);
                return p;
            }
        });

        File photoFile = new File("../sample_data/IMG_20171020_090301.jpg");
        File metadataFile = new File("../sample_data/IMG_20171020_090301");
        assertNotNull(photoService.registerPhoto(p1,photoFile,metadataFile));
        assertNotNull(p1.getId());
        assertNotNull(p1.getImageWidth()); // to see if retrieving EXIF works
        assertNotNull(p1.getCameraFOV()); // to see if retrieving data from json works
    }

    @Test
    public void updatePhotoTest(){
        PhotoEntity p1 = new PhotoEntity();
        p1.setId(1L);
        p1.setUploader(defaultUser);
        p1.setUploadTime(timeZero.minus(Duration.ofDays(20)));
        p1.setTitle("Photo 1");
        p1.setDescription("Description 1");
        p1.setBase64Identifier("thisisab64id");

        PhotoEntity p2 = new PhotoEntity();
        p2.setId(1L);
        p2.setUploader(defaultUser);
        p2.setUploadTime(timeZero.minus(Duration.ofDays(20)));
        p2.setTitle("Photo 2");
        p2.setDescription("Description 2");
        p2.setBase64Identifier("thisisab64id");

        when(photoDaoMock.save(p2)).thenReturn(p2);
        photoService.updatePhoto(p2);
        assertEquals(p1,p2);
    }

}
