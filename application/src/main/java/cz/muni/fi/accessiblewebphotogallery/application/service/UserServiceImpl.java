package cz.muni.fi.accessiblewebphotogallery.application.service;

import cz.muni.fi.accessiblewebphotogallery.application.service.iface.UserService;
import cz.muni.fi.accessiblewebphotogallery.persistence.dao.RegistrationTokenDao;
import cz.muni.fi.accessiblewebphotogallery.persistence.dao.UserDao;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.RegistrationToken;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.inject.Inject;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LogManager.getLogger(UserServiceImpl.class);
    private short HASH_SIZE = 64; // in bytes, the size of password hashes and salts
    private int KDF_IT = 524288; // number of PBKDF2/SHA512 iterations- should do until 2019/2020 at least
    private UserDao userDao;
    private RegistrationTokenDao registrationDao;
    private SecureRandom csprng;

    @Inject
    public UserServiceImpl(UserDao userDao, Mapper mapper, RegistrationTokenDao regDao) {
        this.userDao = userDao;
        this.registrationDao = regDao;
        try {
            csprng = SecureRandom.getInstance("NativePRNGNonBlocking");
            // Non-blocking is fine, see eg. my Bc. thesis
        } catch (NoSuchAlgorithmException nsae) {
            log.catching(nsae);
            log.warn("Native non-blocking PRNG unavailable.");
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                try {
                    csprng = SecureRandom.getInstance("Windows-PRNG");
                    log.info("Windows-PRNG available, acquired SecureRandom instance.");
                } catch (NoSuchAlgorithmException nsaeWindows) {
                    log.catching(nsaeWindows);
                    log.warn("No Windows PRNG found.");
                }
            }
            if (csprng == null) {
                log.info("Using default CSPRNG.");
                csprng = new SecureRandom();
            }
        }
    }

    @Override
    public PageImpl<UserEntity> findAll(Pageable pageable) {
        Page<UserEntity> page = userDao.findAll(pageable);
        PageImpl rv = new PageImpl<>(page.getContent(), pageable, userDao.count());
        return rv;
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public Optional<UserEntity> findByLoginName(String loginName) {
        return userDao.findByLoginName(loginName);
    }

    @Override
    public List<UserEntity> findByScreenNameContainingIgnoreCase(String insensitiveScreenNamePart) {
        return userDao.findByScreenNameContainingIgnoreCase(insensitiveScreenNamePart);
    }

    @Override
    public boolean isAdmin(UserEntity user) {
        Optional<UserEntity> userOpt = userDao.findByEmail(user.getEmail());
        return userOpt.isPresent() && userOpt.get().getAccountState().equals(AccountState.ADMINISTRATOR);
    }

    @Override
    public Pair<UserEntity, String> registerUser(UserEntity user, String password) {
        Pair<byte[], byte[]> hashAndSalt = makeSaltAndHashPass(password);
        if (hashAndSalt == null) {
            return null;
        }
        user.setPasswordHash(hashAndSalt.getFirst());
        user.setPasswordSalt(hashAndSalt.getSecond());
        user = userDao.save(user);
        String token = RandomStringUtils.randomAlphanumeric(32);
        registrationDao.save(new RegistrationToken(user.getEmail(), token));
        return Pair.of(user, token);
    }

    @Override
    public boolean authenticateUser(UserEntity user, String password) {
        Validate.notNull(user);
        Validate.notNull(password);
        return checkPassword(password, user.getPasswordHash(), user.getPasswordSalt());
    }

    @Override
    public UserEntity updateUser(UserEntity user) {
        Validate.notNull(user.getId()); // must exist
        return userDao.save(user);
    }

    @Override
    public boolean confirmUser(String email, String token) {
        Validate.notNull(email);
        Validate.notNull(token);
        Optional<RegistrationToken> regToken = registrationDao.findByEmail(email);
        if(regToken.isPresent() && token.equals(regToken.get().getToken())){
            Optional<UserEntity> userOpt = userDao.findByEmail(email);
            if(!userOpt.isPresent()) {return false;}
            UserEntity user = userOpt.get();
            user.setAccountState(AccountState.USER);
            updateUser(user);
            registrationDao.delete(regToken.get());
            return true;
        }
        return false;
    }

    private Pair<byte[],byte[]> makeSaltAndHashPass(String password){
        byte[] salt = new byte[HASH_SIZE];
        csprng.nextBytes(salt);
        byte[] hash;
        try {
            hash = pbkdf2(password.toCharArray(), salt, KDF_IT, HASH_SIZE);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex){
            log.catching(ex);
            if(ex instanceof NoSuchAlgorithmException) {
                log.error("PBKDF2 failed- no such algorithm.");
            } else{
                log.error("PBKDF2 failed- invalid key spec");
            }
            return null;
        }
        return Pair.of(hash,salt);
    }

    private boolean checkPassword(String password, byte[] expected, byte[] salt){
        Validate.notNull(password);
        Validate.notNull(expected);
        Validate.notNull(salt);
        byte[] actual;
        try {
            actual = pbkdf2(password.toCharArray(),salt,KDF_IT,HASH_SIZE);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.catching(e);
            if(e instanceof NoSuchAlgorithmException){
                log.error("PBKDF2 failed- no such algorithm.");
            } else{
                log.error("PBKDF2 failed- invalid key spec");
            }
            return false;
        }
        return slowEquals(expected,actual);
    }

    private byte[] pbkdf2(char[] password, byte[] salt, int iterations, int nbytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // note: PBEKeySpec takes bits
        PBEKeySpec keySpec = new PBEKeySpec(password,salt,iterations,nbytes*8);
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512").generateSecret(keySpec).getEncoded());
    }

    private boolean slowEquals(byte[] a, byte[] b){
        int diff;
        diff = a.length == b.length? 0:65536;
        for(int i=0;i<a.length && i < b.length;i++){
            if((a[i] ^ b[i]) != 0){
                diff++;
            }
        }
        return diff == 0;
    }

}
