package cz.muni.fi.accessiblewebphotogallery.application.service.iface;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Optional;

/**
 * Interacts directly with UserDao, does most of the computation work required, such as hashing passwords
 */
public interface UserService{

    /**
     * Retrieves all users it can find
     * @return a List containing UserEntity instances, one for each registered user.
     */
    List<UserEntity> findAll();

    /**
     * Looks up a user by database ID
     * @param id database ID to search
     * @return Optional containing a UserEntity e such that e.id == id if such is found,
     * an empty Optional otherwise
     */
    Optional<UserEntity> findById(Long id);

    /**
     * Looks up a user by email
     * @param email email to search
     * @return Optional containing a UserEntity e such that e.email equals email if such is found,
     * an empty Optional otherwise
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Looks up a user by login name
     * @param loginName login name to search
     * @return Optional containing a UserEntity e s.t. e.loginName equals loginName if such is found,
     * an empty optional otherwise
     */
    Optional<UserEntity> findByLoginName(String loginName);

    /**
     * Searches users by screen names, ignoring case
     * @param apxName (part of) user screen name to search, ignoring case
     * @return List of UserEntities whose screenNames match apxName (may be empty)
     */
    List<UserEntity> findByScreenNameApx(String apxName);

    /**
     * Checks whether a user is an administrator
     * @param user user to check
     * @return true if user.accountState == AccountState.ADMINISTRATOR, false otherwise
     */
    boolean isAdmin(UserEntity user);

    /**
     * Computes a password hash and salt and registers a user in the DB
     * @param user User to register
     * @param password User's desired password
     * @return a Pair of the now-updated UserEntity, and the user's registration token(to be emailed)
     */
    Pair<UserEntity,String> registerUser(UserEntity user, String password);

    /**
     * Authenticates a user
     * @param identifier User's email or login name
     * @param password What the (presumed) user typed into the 'Password' login field
     * @return true if authentication was successful, ie. the password hash matches the one saved in the DB,
     * false otherwise
     */
    boolean authenticateUser(String identifier, String password);

    /**
     * Updates a user's information
     * @param user User who wants their info updated
     * @return updated UserEntity as saved by the DB
     */
    UserEntity updateUser(UserEntity user);

    /**
     * Confirms a user's registration when they visit the confirmation link
     * @param email User's email (to which the link was sent)
     * @param token User's registration token, as returned by RegisterUser
     * @return true if the confirmation succeeded, false otherwise (eg. the user already had confirmed their registration)
     */
    boolean confirmUserRegistration(String email, String token);

}
