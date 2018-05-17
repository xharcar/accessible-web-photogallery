package cz.muni.fi.accessiblewebphotogallery.application.facade;


import cz.muni.fi.accessiblewebphotogallery.application.ApplicationConfig;
import cz.muni.fi.accessiblewebphotogallery.application.PhotoGalleryBackendMapper;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.AlbumService;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.AlbumDto;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.facade.facade.AlbumFacade;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AlbumEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
public class AlbumFacadeTest {

    private AlbumFacade albumFacade;
    @Mock
    private AlbumService albumServiceMock;
    private UserDto defaultUser;

    @BeforeAll
    public void init() {
        MockitoAnnotations.initMocks(this);
        albumServiceMock = mock(AlbumService.class);
        albumFacade = new AlbumFacadeImpl(albumServiceMock);
        defaultUser = new UserDto();
        defaultUser.setEmail("ksngmtk@email.org");
        defaultUser.setScreenName("Kusanagi Motoko");
        defaultUser.setAccountState(AccountState.USER);
        defaultUser.setLoginName("ksngmtk");
        defaultUser.setBio("Lorem ipsum dolor sit amet.");
    }

    @Test
    public void findAllTest() {
        AlbumDto a1 = new AlbumDto();
        a1.setOwner(defaultUser);
        a1.setAlbumName("Album 1");
        a1.setId("thisisab64id");
        AlbumDto a2 = new AlbumDto();
        a2.setOwner(defaultUser);
        a2.setAlbumName("Album 2");
        a2.setId("anotherb64id");

        List<AlbumDto> albumList = new ArrayList<>();
        albumList.add(a1);
        albumList.add(a2);
        when(albumServiceMock.findAll()).thenReturn(albumList.stream().map(this::albumDtoToEntity).collect(Collectors.toList()));

        List<AlbumDto> result = albumFacade.findAll();

        assertNotNull(result);
        assertEquals(albumList, result);
    }

    @Test
    public void findByownerTest() {
        UserDto u2 = new UserDto();
        u2.setEmail("skmtm@email.org");
        u2.setScreenName("Sakamoto Mio");
        u2.setAccountState(AccountState.USER);
        u2.setLoginName("skmtm");
        u2.setBio("Consectetuer adipiscing elit.");
        AlbumDto a1 = new AlbumDto();
        a1.setOwner(defaultUser);
        a1.setAlbumName("Album 1");
        a1.setId("thisisab64id");
        AlbumDto a2 = new AlbumDto();
        a2.setOwner(defaultUser);
        a2.setAlbumName("Album 2");
        a2.setId("anotherb64id");
        AlbumDto a3 = new AlbumDto();
        a3.setOwner(u2);
        a3.setAlbumName("Album 3");
        a3.setId("b64idnumber3");

        List<AlbumDto> albumList = new ArrayList<>();
        albumList.add(a1);
        albumList.add(a2);
        when(albumServiceMock.findByOwner(userDtoToEntity(defaultUser))).thenReturn(albumList.stream().map(this::albumDtoToEntity).collect(Collectors.toList()));

        List<AlbumDto> result = albumFacade.findByOwner(defaultUser);

        assertNotNull(result);
        assertEquals(albumList, result);
    }

    private AlbumEntity albumDtoToEntity(AlbumDto dto) {
        return PhotoGalleryBackendMapper.albumDtoToEntity(dto);
    }

    private UserEntity userDtoToEntity(UserDto dto) {
        return PhotoGalleryBackendMapper.userDtoToEntity(dto);
    }
}
