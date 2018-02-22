package cz.muni.fi.accessiblewebphotogallery.persistence.dao;


import cz.muni.fi.accessiblewebphotogallery.persistence.DatabaseConfig;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// UserDao unit tests
@ContextConfiguration(classes = {DatabaseConfig.class})
@ExtendWith(SpringExtension.class)
public class UserDaoTest {

    @Inject
    private UserDao userDao;

    private byte[] dummyhash1 = new byte[]{0x32, (byte) 0xFC,0x5A};
    private byte[] dummyhash2 = new byte[]{0x65,0x7A, (byte) 0xD3};
    private byte[] dummysalt1 = new byte[]{0x3A, (byte) 0xB8, (byte) 0xE1};
    private byte[] dummysalt2 = new byte[]{0x21, (byte) 0x8D,0x7F};


    @BeforeEach
    public void clearDb(){
        userDao.deleteAll();
        assertEquals(0,userDao.count());
    }

    @Test
    public void findByEmailTest(){
        UserEntity user1 = setupAsdfUser1();
        userDao.save(user1);
        UserEntity user2 = setupFdsaUser2();
        userDao.save(user2);
        UserEntity asdf = setupAsdfUser1();
        Optional<UserEntity> foundUser = userDao.findByEmail(asdf.getEmail());
        assertTrue(foundUser.isPresent());
        assertEquals(asdf,foundUser.get());
    }

    @Test
    public void findByLoginNameTest(){
        UserEntity user1 = setupAsdfUser1();
        userDao.save(user1);
        UserEntity user2 = setupFdsaUser2();
        userDao.save(user2);
        UserEntity asdf = setupAsdfUser1();
        Optional<UserEntity> foundUser = userDao.findByLoginName(asdf.getLoginName());
        assertTrue(foundUser.isPresent());
        assertEquals(asdf,foundUser.get());
    }

    @Test
    public void findByScreenNameTest(){
        UserEntity user1 = setupAsdfUser1();
        userDao.save(user1);
        UserEntity user2 = setupFdsaUser2();
        userDao.save(user2);
        Optional<UserEntity> foundUser = userDao.findByScreenName(user1.getScreenName());
        assertTrue(foundUser.isPresent());
        assertEquals(user1,foundUser.get());
    }

    @Test
    public void rejectDuplicitLoginNamesTest(){
        UserEntity user1 = setupAsdfUser1();
        userDao.save(user1);
        UserEntity user2 = setupFdsaUser2();
        user2.setLoginName(user1.getLoginName());
        assertThrows(DataAccessException.class,()->{userDao.save(user2);});
    }

    @Test
    public void doNotRejectDuplicitScreenNamesTest(){
        UserEntity user1 = setupAsdfUser1();
        UserEntity user2 = setupFdsaUser2();
        user2.setScreenName(user1.getScreenName());
        userDao.save(user1);
        userDao.save(user2);
    }

    @Test
    public void rejectDuplicitEmailsTest(){
        UserEntity user1 = setupAsdfUser1();
        UserEntity user2 = setupFdsaUser2();
        user2.setEmail(user1.getEmail());
        userDao.save(user1);
        assertThrows(DataAccessException.class,()->{userDao.save(user2);});
    }

    @Test
    public void rejectNullLoginNameTest(){
        UserEntity user = setupAsdfUser1();
        user.setLoginName(null);
        assertThrows(DataAccessException.class,()->{userDao.save(user);});
    }

    @Test
    public void rejectNullEmailTest(){
        UserEntity user = setupAsdfUser1();
        user.setEmail(null);
        assertThrows(DataAccessException.class,()->{userDao.save(user);});
    }

    @Test
    public void rejectNullPasswordHashTest(){
        UserEntity user = setupAsdfUser1();
        user.setPasswordHash(null);
        assertThrows(DataAccessException.class,()->{userDao.save(user);});
    }

    @Test
    public void rejectNullPasswordSaltTest(){
        UserEntity user = setupAsdfUser1();
        user.setPasswordSalt(null);
        assertThrows(DataAccessException.class,()->{userDao.save(user);});
    }

    @Test
    public void doNotRejectNullScreenName() {
        UserEntity user = setupAsdfUser1();
        user.setScreenName(null);
        userDao.save(user);
    }

    @Test
    public void doNotRejectNullBioTest(){
        UserEntity user = setupAsdfUser1();
        user.setBio(null);
        userDao.save(user);
    }

    @Test
    public void rejectNullAccountState(){
        UserEntity user = setupAsdfUser1();
        user.setAccountState(null);
        assertThrows(DataAccessException.class,()->{userDao.save(user);});
    }

    private UserEntity setupAsdfUser1(){
        UserEntity user1 = new UserEntity();
        user1.setLoginName("asdfuser");
        user1.setAccountState(AccountState.USER);
        user1.setBio("Random user");
        user1.setEmail("user@mail.com");
        user1.setPasswordHash(dummyhash1);
        user1.setPasswordSalt(dummysalt1);
        user1.setScreenName("ASDF User");
        return user1;
    }

    private UserEntity setupFdsaUser2(){
        UserEntity rv = new UserEntity();
        rv.setLoginName("fdsauser");
        rv.setAccountState(AccountState.USER);
        rv.setBio("Not quite random user");
        rv.setEmail("fdsa@email.org");
        rv.setPasswordHash(dummyhash2);
        rv.setPasswordSalt(dummysalt2);
        rv.setScreenName("FDSA User");
        return rv;
    }

}
