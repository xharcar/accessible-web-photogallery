package cz.muni.fi.accessiblewebphotogallery.application.service;

import cz.muni.fi.accessiblewebphotogallery.iface.ApplicationConfig;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.AlbumService;
import cz.muni.fi.accessiblewebphotogallery.persistence.dao.AlbumDao;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AlbumEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.junit.jupiter.api.AfterAll;
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

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ApplicationConfig.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AlbumServiceTest {

    @Inject
    ApplicationConfig cfg;
    private AlbumService albumService;
    @Mock
    private AlbumDao albumDaoMock;
    private UserEntity defaultUser;
    private byte[] dummyhash1 = new byte[]{0x32, (byte) 0xFC, 0x5A};
    private byte[] dummysalt1 = new byte[]{0x3A, (byte) 0xB8, (byte) 0xE1};

    @BeforeAll
    public void init() {
        MockitoAnnotations.initMocks(this);
        albumDaoMock = mock(AlbumDao.class);
        albumService = new AlbumServiceImpl(albumDaoMock, cfg);
        defaultUser = new UserEntity();
        defaultUser.setEmail("abdc@email.org");
        defaultUser.setScreenName("DKXC");
        defaultUser.setAccountState(AccountState.USER);
        defaultUser.setLoginName("xdck");
        defaultUser.setBio("Lorem ipsum dolor sit amet.");
        defaultUser.setPasswordHash(dummyhash1);
        defaultUser.setPasswordSalt(dummysalt1);
    }

    @Test
    public void findAllTest() {
        AlbumEntity a1 = new AlbumEntity();
        a1.setAlbumOwner(defaultUser);
        a1.setAlbumName("Album 1");
        a1.setBase64Identifier("thisisab64id");
        AlbumEntity a2 = new AlbumEntity();
        a2.setAlbumOwner(defaultUser);
        a2.setAlbumName("Album 2");
        a2.setBase64Identifier("anotherb64id");

        List<AlbumEntity> albumList = new ArrayList<>();
        albumList.add(a1);
        albumList.add(a2);
        when(albumDaoMock.findAll()).thenReturn(albumList);

        List<AlbumEntity> result = albumService.findAll();
        assertNotNull(result);
        assertEquals(albumList, result);
    }

    @Test
    public void findByOwnerTest() {
        UserEntity user2 = new UserEntity();
        user2.setEmail("jebo@email.org");
        user2.setScreenName("JeboZLesa");
        user2.setAccountState(AccountState.USER);
        user2.setLoginName("jebo85");
        user2.setBio("Consectetuer adipiscing elit.");
        user2.setPasswordHash(dummysalt1);
        user2.setPasswordSalt(dummyhash1);
        AlbumEntity a1 = new AlbumEntity();
        a1.setAlbumOwner(defaultUser);
        a1.setAlbumName("Album 1");
        a1.setBase64Identifier("thisisab64id");
        AlbumEntity a2 = new AlbumEntity();
        a2.setAlbumOwner(user2);
        a2.setAlbumName("Album 2");
        a2.setBase64Identifier("anotherb64id");

        List<AlbumEntity> albumList = new ArrayList<>();
        albumList.add(a1);
        when(albumDaoMock.findByAlbumOwner(defaultUser)).thenReturn(albumList);

        List<AlbumEntity> result = albumService.findByAlbumOwner(defaultUser);
        assertNotNull(result);
        assertEquals(albumList, result);
    }

    @Test
    public void createAlbumTest() {
        when(albumDaoMock.save(any(AlbumEntity.class))).then(new Answer<AlbumEntity>() {
            @Override
            public AlbumEntity answer(InvocationOnMock invocation) throws Throwable {
                AlbumEntity rv = invocation.getArgument(0);
                rv.setId(1L);
                return rv;
            }
        });
        AlbumEntity entity = albumService.createAlbum(defaultUser, "Album 342");
        File albumFile = new File(cfg.getAlbumDirectory() + File.separator + entity.getBase64Identifier() + ".txt");
        assertNotNull(entity.getId());
        assertTrue(albumFile.exists());
    }

    @Test
    public void saveAlbumTest() {
        AlbumEntity a1 = new AlbumEntity();
        a1.setId(1L);
        a1.setAlbumOwner(defaultUser);
        a1.setAlbumName("Album 1");
        a1.setBase64Identifier("thisisab64id");
        AlbumEntity a2 = new AlbumEntity();
        a2.setId(1L);
        a2.setAlbumOwner(defaultUser);
        a2.setAlbumName("Chernobyl 2018");
        a2.setBase64Identifier("thisisab64id");

        when(albumDaoMock.save(a2)).thenReturn(a2);
        albumService.updateAlbum(a2);
        assertEquals(a1, a2);
    }

    @Test
    public void addPhotoTest() {
        AlbumEntity a1 = new AlbumEntity();
        a1.setId(1L);
        a1.setAlbumOwner(defaultUser);
        a1.setAlbumName("Album 1");
        a1.setBase64Identifier("thisisab64id");
        File albumFile = new File(cfg.getAlbumDirectory() + File.separator + a1.getBase64Identifier() + ".txt");
        try {
            assertTrue(albumFile.createNewFile());
        } catch (IOException e) {
            fail("Couldn't create album file.");
        }
        String b64 = "photo00b64id";
        assertTrue(albumService.addPhotoToAlbum(a1, b64));
        List<String> photoList = null;
        try {
            photoList = Files.readAllLines(albumFile.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            fail("IOException reading album file");
        }
        assertNotNull(photoList);
        assertEquals(1, photoList.size());
        assertTrue(photoList.contains(b64));
    }

    @Test
    public void removePhotoTest() {
        AlbumEntity a1 = new AlbumEntity();
        a1.setId(1L);
        a1.setAlbumOwner(defaultUser);
        a1.setAlbumName("Album 1");
        a1.setBase64Identifier("thisisab64id");
        File albumFile = new File(cfg.getAlbumDirectory() + File.separator + a1.getBase64Identifier() + ".txt");
        String b64 = "photo00b64id";
        if (!albumFile.exists()) {
            try {
                assertTrue(albumFile.createNewFile());
            } catch (IOException e) {
                fail("Couldn't create album file.");
            }

            assertTrue(albumService.addPhotoToAlbum(a1, b64));
            List<String> photoList = null;
            try {
                photoList = Files.readAllLines(albumFile.toPath(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                fail("IOException reading album file");
            }
            assertNotNull(photoList);
            assertEquals(1, photoList.size());
            assertTrue(photoList.contains(b64));
        }
        assertTrue(albumService.removePhotoFromAlbum(a1, b64));
        assertTrue(albumFile.exists());
        List<String> resultList = null;
        try {
            resultList = Files.readAllLines(albumFile.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            fail("IOException reading album file");
        }
        assertTrue(resultList.isEmpty());
    }

    @Test
    public void listPhotosTest() {
        AlbumEntity a1 = new AlbumEntity();
        a1.setId(1L);
        a1.setAlbumOwner(defaultUser);
        a1.setAlbumName("Album 1");
        a1.setBase64Identifier("thisisab64id");
        File albumFile = new File(cfg.getAlbumDirectory() + File.separator + a1.getBase64Identifier() + ".txt");
        String b64 = "photo00b64id";
        if (!albumFile.exists()) {
            try {
                assertTrue(albumFile.createNewFile());
            } catch (IOException e) {
                fail("Couldn't create album file.");
            }
        }
        assertTrue(albumService.addPhotoToAlbum(a1, b64));
        List<String> photoList = albumService.listPhotosInAlbum(a1);
        assertNotNull(photoList);
        assertEquals(1, photoList.size());
        assertTrue(photoList.contains(b64));
    }

    @Test
    public void findByBase64IdTest() {
        AlbumEntity a1 = new AlbumEntity();
        a1.setAlbumOwner(defaultUser);
        a1.setAlbumName("Album 1");
        a1.setBase64Identifier("thisisab64id");
        AlbumEntity a2 = new AlbumEntity();
        a2.setAlbumOwner(defaultUser);
        a2.setAlbumName("Album 2");
        a2.setBase64Identifier("anotherb64id");

        when(albumDaoMock.findByBase64Identifier("thisisab64id")).thenReturn(Optional.of(a1));

        Optional<AlbumEntity> resOpt = albumService.findByBase64Id("thisisab64id");
        assertNotNull(resOpt);
        assertTrue(resOpt.isPresent());
        assertEquals(a1, resOpt.get());
    }

    @AfterAll
    public void clearTestFile() {
        File albumFile = new File(cfg.getAlbumDirectory() + File.separator + "thisisab64id.txt");
        if (albumFile.exists()) {
            albumFile.delete();
        }
    }

}
