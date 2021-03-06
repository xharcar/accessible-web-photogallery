package cz.muni.fi.accessiblewebphotogallery.application.facade;

import cz.muni.fi.accessiblewebphotogallery.application.ApplicationConfig;
import cz.muni.fi.accessiblewebphotogallery.application.PhotoGalleryBackendMapper;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.UserService;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.facade.facade.UserFacade;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
public class UserFacadeTest {

    @Autowired
    ApplicationConfig cfg;
    private UserFacade userFacade;
    @Mock
    private UserService userServiceMock;
    private byte[] dummyhash1 = new byte[]{0x32, (byte) 0xFC, 0x5A};
    private byte[] dummysalt1 = new byte[]{0x3A, (byte) 0xB8, (byte) 0xE1};


    @BeforeAll
    public void init() {
        MockitoAnnotations.initMocks(this);
        userServiceMock = mock(UserService.class);
        userFacade = new UserFacadeImpl(userServiceMock);
    }

    @Test
    public void findAllTest() {
        UserDto u1 = new UserDto();
        u1.setEmail("abdc@email.org");
        u1.setScreenName("DKXC");
        u1.setAccountState(AccountState.USER);
        u1.setLoginName("xdck");
        u1.setBio("Lorem ipsum dolor sit amet.");

        UserDto u2 = new UserDto();
        u2.setEmail("atks@email.org");
        u2.setScreenName("SakMto");
        u2.setAccountState(AccountState.USER);
        u2.setLoginName("motkas");
        u2.setBio("Consectetuer adipiscing elit.");

        List<UserDto> userList = new ArrayList<>();
        userList.add(u1);
        userList.add(u2);

        when(userServiceMock.findAll()).thenReturn(userList.stream().map(this::userDtoToEntity).collect(Collectors.toList()));
        List<UserDto> resultList = userFacade.findAll();

        assertNotNull(resultList);
        assertEquals(2, resultList.size());
        assertEquals(userList, resultList);
    }

    @Test
    public void findByIdTest() {
        UserDto u1 = new UserDto();
        u1.setEmail("abdc@email.org");
        u1.setScreenName("DKXC");
        u1.setAccountState(AccountState.USER);
        u1.setLoginName("xdck");
        u1.setBio("Lorem ipsum dolor sit amet.");
        u1.setId(1L);

        when(userServiceMock.findById(1L)).thenReturn(Optional.of(userDtoToEntity(u1)));
        Optional<UserDto> dtoOptional = userFacade.findById(1L);

        assertNotNull(dtoOptional);
        assertTrue(dtoOptional.isPresent());
        assertEquals(u1, dtoOptional.get());
    }

    @Test
    public void findByEmailTest() {
        UserDto u1 = new UserDto();
        u1.setEmail("abdc@email.org");
        u1.setScreenName("DKXC");
        u1.setAccountState(AccountState.USER);
        u1.setLoginName("xdck");
        u1.setBio("Lorem ipsum dolor sit amet.");

        when(userServiceMock.findByEmail("abdc@email.org")).thenReturn(Optional.of(userDtoToEntity(u1)));
        Optional<UserDto> dtoOptional = userFacade.findByEmail("abdc@email.org");

        assertNotNull(dtoOptional);
        assertTrue(dtoOptional.isPresent());
        assertEquals(u1, dtoOptional.get());
    }

    @Test
    public void findByLoginNameTest() {
        UserDto u1 = new UserDto();
        u1.setEmail("abdc@email.org");
        u1.setScreenName("DKXC");
        u1.setAccountState(AccountState.USER);
        u1.setLoginName("xdck");
        u1.setBio("Lorem ipsum dolor sit amet.");

        when(userServiceMock.findByLoginName("xdck")).thenReturn(Optional.of(userDtoToEntity(u1)));
        Optional<UserDto> dtoOptional = userFacade.findByLoginName("xdck");

        assertNotNull(dtoOptional);
        assertTrue(dtoOptional.isPresent());
        assertEquals(u1, dtoOptional.get());
    }

    @Test
    public void findByScreenNameEtcTest() {
        UserDto u1 = new UserDto();
        u1.setEmail("ksngmtk@email.org");
        u1.setScreenName("Kusanagi Motoko");
        u1.setAccountState(AccountState.USER);
        u1.setLoginName("ksngmtk");
        u1.setBio("Lorem ipsum dolor sit amet.");

        UserDto u2 = new UserDto();
        u2.setEmail("skmtm@email.org");
        u2.setScreenName("Sakamoto Mio");
        u2.setAccountState(AccountState.USER);
        u2.setLoginName("skmtm");
        u2.setBio("Consectetuer adipiscing elit.");

        List<UserDto> userList = new ArrayList<>();
        userList.add(u1);

        when(userServiceMock.findByScreenNameApx("moto"))
                .thenReturn(userList.stream().map(this::userDtoToEntity).collect(Collectors.toList()));
        List<UserDto> resultList = userFacade.findByScreenNameApx("moto");

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertEquals(u1, resultList.get(0));
    }

    @Test
    public void authenticateTest() {
        UserDto u1 = new UserDto();
        u1.setEmail("ksngmtk@email.org");
        u1.setScreenName("Kusanagi Motoko");
        u1.setAccountState(AccountState.USER);
        u1.setLoginName("ksngmtk");
        u1.setBio("Lorem ipsum dolor sit amet.");

        UserEntity u1e = userDtoToEntity(u1);
        when(userServiceMock.findByEmail("ksngmtk@email.org")).thenReturn(Optional.of(u1e));
        when(userServiceMock.findByLoginName("ksngmtk")).thenReturn(Optional.of(u1e));
        when(userServiceMock.authenticateUser(u1e.getEmail(), "password")).thenReturn(Pair.of(true, Optional.of(u1e)));
        when(userServiceMock.authenticateUser(u1e.getLoginName(), "password")).thenReturn(Pair.of(true, Optional.of(u1e)));
        when(userServiceMock.authenticateUser(u1e.getEmail(), "not password")).thenReturn(Pair.of(false, Optional.empty()));
        when(userServiceMock.authenticateUser(u1e.getLoginName(), "not password")).thenReturn(Pair.of(false, Optional.empty()));

        assertTrue(userFacade.authenticateUser("ksngmtk@email.org", "password").getFirst());
        assertFalse(userFacade.authenticateUser("ksngmtk@email.org", "not password").getFirst());
        assertTrue(userFacade.authenticateUser("ksngmtk", "password").getFirst());
        assertFalse(userFacade.authenticateUser("ksngmtk", "not password").getFirst());
    }

    @Test
    public void registerUserTest() {
        UserDto u1 = new UserDto();
        u1.setEmail("ksngmtk@email.org");
        u1.setScreenName("Kusanagi Motoko");
        u1.setAccountState(AccountState.USER);
        u1.setLoginName("ksngmtk");
        u1.setBio("Lorem ipsum dolor sit amet.");


        UserEntity u1e = userDtoToEntity(u1);
        when(userServiceMock.registerUser(u1e, "password")).then(new Answer<Pair<UserEntity, String>>() {
            public Pair<UserEntity, String> answer(InvocationOnMock invocation) throws Throwable {
                UserEntity u = (UserEntity) invocation.getArgument(0);
                u.setId(1L);
                u.setPasswordHash(dummyhash1);
                u.setPasswordSalt(dummysalt1);
                String x = Integer.toHexString(u.hashCode());
                return Pair.of(u, x);
            }
        });
        Pair<UserDto, String> result = userFacade.registerUser(u1, "password");
        assertNotNull(result);
        assertNotNull(u1.getId());
        assertNotNull(u1.getPasswordHash());
        assertNotNull(u1.getPasswordSalt());
        assertNotEquals("password", u1.getPasswordHash());
    }

    @Test
    public void adminCheckTest() {
        UserDto u1 = new UserDto();
        u1.setEmail("ksngmtk@email.org");
        u1.setScreenName("Kusanagi Motoko");
        u1.setAccountState(AccountState.ADMINISTRATOR);
        u1.setLoginName("ksngmtk");
        u1.setBio("Lorem ipsum dolor sit amet.");

        UserDto u2 = new UserDto();
        u2.setEmail("skmtm@email.org");
        u2.setScreenName("Sakamoto Mio");
        u2.setAccountState(AccountState.USER);
        u2.setLoginName("skmtm");
        u2.setBio("Consectetuer adipiscing elit.");

        UserEntity u1e_a = userDtoToEntity(u1);
        UserEntity u2e_u = userDtoToEntity(u2);
        when(userServiceMock.isAdmin(u1e_a)).thenReturn(true);
        when(userServiceMock.isAdmin(u2e_u)).thenReturn(false);

        assertTrue(userFacade.isAdmin(u1));
        assertFalse(userFacade.isAdmin(u2));
    }

    @Test
    public void updateUserTest() {
        UserDto u1 = new UserDto();
        u1.setEmail("ksngmtk@email.org");
        u1.setScreenName("Kusanagi Motoko");
        u1.setAccountState(AccountState.ADMINISTRATOR);
        u1.setLoginName("ksngmtk");
        u1.setBio("Lorem ipsum dolor sit amet.");
        u1.setId(1L);
        UserEntity u1e = userDtoToEntity(u1);
        when(userServiceMock.updateUser(u1e)).thenReturn(u1e);
        UserDto updated = userFacade.updateUser(u1);
        assertEquals(u1, updated);
    }

    private UserEntity userDtoToEntity(UserDto dto) {
        return PhotoGalleryBackendMapper.userDtoToEntity(dto);
    }

}
