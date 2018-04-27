package cz.muni.fi.accessiblewebphotogallery.application.service;


import cz.muni.fi.accessiblewebphotogallery.application.ApplicationConfig;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.UserService;
import cz.muni.fi.accessiblewebphotogallery.persistence.dao.RegistrationTokenDao;
import cz.muni.fi.accessiblewebphotogallery.persistence.dao.UserDao;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.RegistrationToken;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.util.Pair;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ApplicationConfig.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {

    private UserService userService;
    @Mock
    private UserDao userDaoMock;

    private byte[] dummyhash1 = new byte[]{0x32, (byte) 0xFC,0x5A};
    private byte[] dummyhash2 = new byte[]{0x65,0x7A, (byte) 0xD3};
    private byte[] dummysalt1 = new byte[]{0x3A, (byte) 0xB8, (byte) 0xE1};
    private byte[] dummysalt2 = new byte[]{0x21, (byte) 0x8D,0x7F};

    @BeforeAll
    public void init(){
        MockitoAnnotations.initMocks(this);
        RegistrationTokenDao regDaoMock = mock(RegistrationTokenDao.class);
        when(regDaoMock.findByEmail(any()))
                .thenReturn(Optional.of(new RegistrationToken("email","USER_TOKEN")));
        userService = new UserServiceImpl(userDaoMock, regDaoMock);
    }


    @Test
    public void findAllTest(){
        UserEntity u1 = new UserEntity();
        u1.setEmail("abdc@email.org");
        u1.setScreenName("DKXC");
        u1.setAccountState(AccountState.USER);
        u1.setLoginName("xdck");
        u1.setBio("Lorem ipsum dolor sit amet.");
        u1.setPasswordHash(dummyhash1);
        u1.setPasswordSalt(dummysalt1);

        UserEntity u2 = new UserEntity();
        u2.setEmail("atks@email.org");
        u2.setScreenName("SakMto");
        u2.setAccountState(AccountState.USER);
        u2.setLoginName("motkas");
        u2.setBio("Consectetuer adipiscing elit.");
        u2.setPasswordHash(dummyhash2);
        u2.setPasswordSalt(dummysalt2);

        List<UserEntity> testUsers = new ArrayList<>();
        testUsers.add(u1);
        testUsers.add(u2);
        when(userDaoMock.findAll()).thenReturn(testUsers);
        List<UserEntity> resultList = userService.findAll();
        assertNotNull(resultList);
        assertEquals(testUsers,resultList);
    }

    @Test
    public void findByEmailTest(){
        UserEntity u1 = new UserEntity();
        u1.setEmail("abdc@email.org");
        u1.setScreenName("DKXC");
        u1.setAccountState(AccountState.USER);
        u1.setLoginName("xdck");
        u1.setBio("Lorem ipsum dolor sit amet.");
        u1.setPasswordHash(dummyhash1);
        u1.setPasswordSalt(dummysalt1);

        when(userDaoMock.findByEmail("abdc@email.org")).thenReturn(Optional.of(u1));
        Optional<UserEntity> userOpt = userService.findByEmail("abdc@email.org");
        assertTrue(userOpt.isPresent());
        assertEquals(u1,userOpt.get());
    }

    @Test
    public void findByLoginNameTest(){
        UserEntity u1 = new UserEntity();
        u1.setEmail("abdc@email.org");
        u1.setScreenName("DKXC");
        u1.setAccountState(AccountState.USER);
        u1.setLoginName("xdck");
        u1.setBio("Lorem ipsum dolor sit amet.");
        u1.setPasswordHash(dummyhash1);
        u1.setPasswordSalt(dummysalt1);

        when(userDaoMock.findByLoginName("xdck")).thenReturn(Optional.of(u1));
        Optional<UserEntity> userOpt = userService.findByLoginName("xdck");
        assertTrue(userOpt.isPresent());
        assertEquals(u1,userOpt.get());
    }

    @Test
    public void findByScreenNameCaselessTest(){
        UserEntity u1 = new UserEntity();
        u1.setEmail("abdc@email.org");
        u1.setScreenName("Mandall Runroe");
        u1.setAccountState(AccountState.USER);
        u1.setLoginName("xdck");
        u1.setBio("Lorem ipsum dolor sit amet.");
        u1.setPasswordHash(dummyhash1);
        u1.setPasswordSalt(dummysalt1);

        when(userDaoMock.findByScreenNameContainingIgnoreCase("manda")).then(new Answer<List<UserEntity>>() {
            @Override
            public List<UserEntity> answer(InvocationOnMock invocation) {
                List<UserEntity> rv = new ArrayList<>();
                rv.add(u1);
                return rv;
            }
        });
        List<UserEntity> foundList = userService.findByScreenNameContainingIgnoreCase("manda");
        assertFalse(foundList.isEmpty());
        assertEquals(1,foundList.size());
        assertEquals(u1,foundList.get(0));
    }

    @Test
    public void registerUserTest(){
        UserEntity u1 = new UserEntity();
        u1.setEmail("abdc@email.org");
        u1.setScreenName("DKXC");
        u1.setAccountState(AccountState.USER);
        u1.setLoginName("xdck");
        u1.setBio("Lorem ipsum dolor sit amet.");
        u1.setPasswordHash(dummyhash1);
        u1.setPasswordSalt(dummysalt1);

        when(userDaoMock.save(u1)).then(new Answer<UserEntity>() {
            public UserEntity answer(InvocationOnMock invocation) throws Throwable {
                UserEntity u = (UserEntity) invocation.getArguments()[0];
                u.setId(1L);
                return u;
            }
        });
        assertNotNull(userService.registerUser(u1,"password1"));
        assertNotNull(u1.getId());
        assertNotNull(u1.getPasswordHash());
        assertNotNull(u1.getPasswordSalt());
        assertNotEquals("password1",u1.getPasswordHash());
    }

    @Test
    public void authenticateTest(){
        Pair<byte[],byte[]> hashAndSalt = makeSaltAndHashPass("password");
        assertNotNull(hashAndSalt);
        byte[] hash = hashAndSalt.getFirst();
        byte[] salt = hashAndSalt.getSecond();
        UserEntity u1 = new UserEntity();
        u1.setEmail("abdc@email.org");
        u1.setScreenName("DKXC");
        u1.setAccountState(AccountState.USER);
        u1.setLoginName("xdck");
        u1.setBio("Lorem ipsum dolor sit amet.");
        u1.setPasswordHash(hash);
        u1.setPasswordSalt(salt);
        assertTrue(userService.authenticateUser(u1,"password"));
        assertFalse(userService.authenticateUser(u1,"notpassword"));
    }

    @Test
    public void updateUserTest(){
        UserEntity u0 = new UserEntity();
        u0.setId(1L);
        u0.setEmail("abdc@email.org");
        u0.setScreenName("DKXC");
        u0.setAccountState(AccountState.USER);
        u0.setLoginName("xdck");
        u0.setBio("Lorem ipsum dolor sit amet.");
        u0.setPasswordHash(dummyhash1);
        u0.setPasswordSalt(dummysalt1);

        UserEntity u1 = new UserEntity();
        u1.setId(1L);
        u1.setEmail("abdc@email.org");
        u1.setScreenName("CDKX");
        u1.setAccountState(AccountState.USER);
        u1.setLoginName("xdck");
        u1.setBio("Lorem ipsum dolor sit amet, consectetuer adpisicing elit.");
        u1.setPasswordHash(dummyhash1);
        u1.setPasswordSalt(dummysalt1);

        when(userDaoMock.save(u1)).thenReturn(u1);
        userService.updateUser(u1);
        assertEquals(u0,u1);
    }

    @Test
    public void updateUserNullIdTest(){
        UserEntity u1 = new UserEntity();
        u1.setEmail("abdc@email.org");
        u1.setScreenName("DKXC");
        u1.setAccountState(AccountState.USER);
        u1.setLoginName("xdck");
        u1.setBio("Lorem ipsum dolor sit amet.");
        u1.setPasswordHash(dummyhash1);
        u1.setPasswordSalt(dummysalt1);
        when(userDaoMock.save(u1)).thenReturn(u1);

        assertThrows(NullPointerException.class,()->{userService.updateUser(u1);});
    }

    @Test
    public void adminCheckTest(){
        UserEntity u1 = new UserEntity();
        u1.setEmail("abdc@email.org");
        u1.setScreenName("DKXC");
        u1.setAccountState(AccountState.USER);
        u1.setLoginName("xdck");
        u1.setBio("Lorem ipsum dolor sit amet.");
        u1.setPasswordHash(dummyhash1);
        u1.setPasswordSalt(dummysalt1);

        UserEntity u2 = new UserEntity();
        u2.setEmail("abdc@email.org");
        u2.setScreenName("SakMto");
        u2.setAccountState(AccountState.ADMINISTRATOR);
        u2.setLoginName("motkas");
        u2.setBio("Consectetuer adipiscing elit.");
        u2.setPasswordHash(dummyhash2);
        u2.setPasswordSalt(dummysalt2);

        when(userDaoMock.findByEmail("abdc@email.org")).thenReturn(Optional.of(u1)).thenReturn(Optional.of(u2));

        assertFalse(userService.isAdmin(u1));
        assertTrue(userService.isAdmin(u2));
    }


    private short HASH_SIZE = 64; // in bytes, the size of password hashes and salts
    private int KDF_IT = 524288; // number of PBKDF2/SHA512 iterations- should do until 2019/2020 at least
    private Pair<byte[],byte[]> makeSaltAndHashPass(String password){
        Random random = new Random(); // for testing, not having a real CSPRNG isn't a problem;
        // of course, the actual implementation does use one
        byte[] salt = new byte[HASH_SIZE];
        random.nextBytes(salt);
        byte[] hash;
        try {
            hash = pbkdf2(password.toCharArray(), salt, KDF_IT, HASH_SIZE);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e){
            fail(e.getMessage());
            return null;
        }
        return Pair.of(hash,salt);
    }

    private byte[] pbkdf2(char[] password, byte[] salt, int iterations, int nbytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // note: PBEKeySpec takes bits
        PBEKeySpec keySpec = new PBEKeySpec(password,salt,iterations,nbytes*8);
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512").generateSecret(keySpec).getEncoded();
    }


}
