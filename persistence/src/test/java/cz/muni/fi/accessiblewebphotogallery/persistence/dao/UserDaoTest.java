package cz.muni.fi.accessiblewebphotogallery.persistence.dao;


import cz.muni.fi.accessiblewebphotogallery.persistence.DatabaseConfig;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// UserDao unit tests
@ContextConfiguration(classes = {DatabaseConfig.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @BeforeEach
    @AfterAll
    public void clearDb() {
        userDao.deleteAll();
        assertEquals(0, userDao.count());
    }

    @Test
    public void findByEmailTest() {
        UserEntity user1 = setupAsdfUser1();
        user1 = userDao.save(user1);
        UserEntity user2 = setupFdsaUser2();
        userDao.save(user2);
        Optional<UserEntity> foundUser = userDao.findByEmail(user1.getEmail());
        assertTrue(foundUser.isPresent());
        assertEquals(user1, foundUser.get());
    }

    @Test
    public void findByLoginNameTest() {
        UserEntity user1 = setupAsdfUser1();
        user1 = userDao.save(user1);
        UserEntity user2 = setupFdsaUser2();
        userDao.save(user2);
        Optional<UserEntity> foundUser = userDao.findByLoginName(user1.getLoginName());
        assertTrue(foundUser.isPresent());
        assertEquals(user1, foundUser.get());
    }

    @Test
    public void findByScreenNameContainingIgnoreCaseTest() {
        UserEntity user1 = setupAsdfUser1();
        user1 = userDao.save(user1);
        UserEntity user2 = setupFdsaUser2();
        user2 = userDao.save(user2);
        List<UserEntity> found = userDao.findByScreenNameContainingIgnoreCase(user1.getScreenName());
        assertEquals(1, found.size());
        assertEquals(user1, found.get(0));
        List<UserEntity> found2 = userDao.findByScreenNameContainingIgnoreCase(user2.getScreenName().toLowerCase().substring(0, 2));// looking for "fds"
        assertEquals(1, found2.size());
        assertEquals(user2, found2.get(0));
    }

    @Test
    public void rejectDuplicitLoginNamesTest() {
        UserEntity user1 = setupAsdfUser1();
        user1 = userDao.save(user1);
        UserEntity user2 = setupFdsaUser2();
        user2.setLoginName(user1.getLoginName());
        assertThrows(DataAccessException.class, () -> {
            userDao.save(user2);
        });
    }

    @Test
    public void doNotRejectDuplicitScreenNamesTest() {
        UserEntity user1 = setupAsdfUser1();
        UserEntity user2 = setupFdsaUser2();
        user2.setScreenName(user1.getScreenName());
        userDao.save(user1);
        userDao.save(user2);
    }

    @Test
    public void rejectDuplicitEmailsTest() {
        UserEntity user1 = setupAsdfUser1();
        UserEntity user2 = setupFdsaUser2();
        user2.setEmail(user1.getEmail());
        userDao.save(user1);
        assertThrows(DataAccessException.class, () -> {
            userDao.save(user2);
        });
    }

    @Test
    public void rejectNullLoginNameTest() {
        UserEntity user = setupAsdfUser1();
        user.setLoginName(null);
        assertThrows(DataAccessException.class, () -> {
            userDao.save(user);
        });
    }

    @Test
    public void rejectNullEmailTest() {
        UserEntity user = setupAsdfUser1();
        user.setEmail(null);
        assertThrows(DataAccessException.class, () -> {
            userDao.save(user);
        });
    }

    @Test
    public void rejectNullPasswordHashTest() {
        UserEntity user = setupAsdfUser1();
        user.setPasswordHash(null);
        assertThrows(DataAccessException.class, () -> {
            userDao.save(user);
        });
    }

    @Test
    public void rejectNullPasswordSaltTest() {
        UserEntity user = setupAsdfUser1();
        user.setPasswordSalt(null);
        assertThrows(DataAccessException.class, () -> {
            userDao.save(user);
        });
    }

    @Test
    public void doNotRejectNullScreenName() {
        UserEntity user = setupAsdfUser1();
        user.setScreenName(null);
        userDao.save(user);
    }

    @Test
    public void doNotRejectNullBioTest() {
        UserEntity user = setupAsdfUser1();
        user.setBio(null);
        userDao.save(user);
    }

    @Test
    public void rejectNullAccountState() {
        UserEntity user = setupAsdfUser1();
        user.setAccountState(null);
        assertThrows(DataAccessException.class, () -> {
            userDao.save(user);
        });
    }

    @Test
    public void updateUserTest() {
        UserEntity user = setupAsdfUser1();
        user = userDao.save(user);
        String newScreenName = "Alexander Nevsky";
        user.setScreenName(newScreenName);
        user = userDao.save(user);
        List<UserEntity> found = userDao.findByScreenNameContainingIgnoreCase("alex");
        assertEquals(1, found.size());
        assertEquals(user, found.get(0));
    }

    @Test
    public void updateUserTest2() {
        UserEntity user = setupAsdfUser1();
        user = userDao.save(user);
        String newEmail = "alexnevsky@vkontakte.ru";
        user.setEmail(newEmail);
        user = userDao.save(user);
        Optional<UserEntity> found = userDao.findByEmail(newEmail);
        assertTrue(found.isPresent());
        assertEquals(user, found.get());
    }

    public static UserEntity setupAsdfUser1() {
        UserEntity user1 = new UserEntity();
        user1.setLoginName("asdfuser");
        user1.setAccountState(AccountState.USER);
        user1.setBio("Random user");
        user1.setEmail("user@mail.com");
        user1.setPasswordHash(new byte[]{0x32, (byte) 0xFC, 0x5A});
        user1.setPasswordSalt(new byte[]{0x3A, (byte) 0xB8, (byte) 0xE1});
        user1.setScreenName("ASDF User");
        return user1;
    }

    public static UserEntity setupFdsaUser2() {
        UserEntity rv = new UserEntity();
        rv.setLoginName("fdsauser");
        rv.setAccountState(AccountState.USER);
        rv.setBio("Not quite random user");
        rv.setEmail("fdsa@email.org");
        rv.setPasswordHash(new byte[]{0x65, 0x7A, (byte) 0xD3});
        rv.setPasswordSalt(new byte[]{0x21, (byte) 0x8D, 0x7F});
        rv.setScreenName("FDSA User");
        return rv;
    }

}
