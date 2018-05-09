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
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.inject.Inject;
import java.security.GeneralSecurityException;
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
    public UserServiceImpl(UserDao userDao, RegistrationTokenDao regDao) {
        this.userDao = userDao;
        this.registrationDao = regDao;
        try {
            csprng = SecureRandom.getInstance("NativePRNGNonBlocking");
            // Non-blocking is fine, see eg. my Bc. thesis
        } catch (NoSuchAlgorithmException nsae) {
            // just so it doesn't propagate further
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                try {
                    csprng = SecureRandom.getInstance("Windows-PRNG");
                } catch (NoSuchAlgorithmException ignored) {
                    // same as above
                }
            }
            if (csprng == null) {
                csprng = new SecureRandom(); // if all else fails, get a default one
            }
        }
        log.info("CSPRNG Algorithm: " + csprng.getAlgorithm());
        // no need to log all the steps that led to this CSPRNG being chosen, we just need to know
        // which one we got
    }

    @Override
    public List<UserEntity> findAll() {
        return userDao.findAll();
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
    public List<UserEntity> findByScreenNameApx(String apxName) {
        return userDao.findByScreenNameContainingIgnoreCase(apxName);
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
    public Pair<Boolean, Optional<UserEntity>> authenticateUser(String identifier, String password) {
        Validate.notNull(identifier);
        Validate.notNull(password);
        Optional<UserEntity> presumedUser;
        if (identifier.contains("@")) {
            presumedUser = findByLoginName(identifier);
        } else {
            presumedUser = findByEmail(identifier);
        }
        if(!presumedUser.isPresent() || !checkPassword(password,presumedUser.get().getPasswordHash(),presumedUser.get().getPasswordSalt())){
            return Pair.of(false, Optional.empty());
        }
        return Pair.of(true,presumedUser);
    }

    @Override
    public UserEntity updateUser(UserEntity user) {
        Validate.notNull(user.getId()); // must exist
        return userDao.save(user);
    }

    @Override
    public boolean confirmUserRegistration(String email, String token) {
        Validate.notNull(email);
        Validate.notNull(token);
        Optional<RegistrationToken> regTokenOpt = registrationDao.findByEmail(email);
        if(regTokenOpt.isPresent() && token.equals(regTokenOpt.get().getToken())){
            Optional<UserEntity> userOpt = userDao.findByEmail(email);
            if(!userOpt.isPresent()) {return false;}
            UserEntity user = userOpt.get();
            user.setAccountState(AccountState.USER);
            updateUser(user);
            registrationDao.delete(regTokenOpt.get());
            return true;
        }
        return false;
    }

    @Override
    public void deleteUser(UserEntity user) {
        userDao.delete(user);
    }

    private Pair<byte[],byte[]> makeSaltAndHashPass(String password){
        byte[] salt = new byte[HASH_SIZE];
        csprng.nextBytes(salt);
        byte[] hash;
        try {
            hash = pbkdf2(password.toCharArray(), salt, KDF_IT, HASH_SIZE);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e){
            handlePBKDF2Fail(e);
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
            handlePBKDF2Fail(e);
            return false;
        }
        return slowEquals(expected,actual);
    }

    private byte[] pbkdf2(char[] password, byte[] salt, int iterations, int nbytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // note: PBEKeySpec takes bits
        PBEKeySpec keySpec = new PBEKeySpec(password,salt,iterations,nbytes*8);
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512").generateSecret(keySpec).getEncoded();
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

    private void handlePBKDF2Fail(GeneralSecurityException gse){
        log.catching(gse);
        log.error(gse.getMessage());
        if(gse instanceof NoSuchAlgorithmException){
            log.error("PBKDF2 failed- no such algorithm.");
        } else{
            log.error("PBKDF2 failed- invalid key spec");
        }
    }

}
