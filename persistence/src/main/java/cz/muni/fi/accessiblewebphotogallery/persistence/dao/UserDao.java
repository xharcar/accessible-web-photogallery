package cz.muni.fi.accessiblewebphotogallery.persistence.dao;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserDao extends JpaRepository<UserEntity,Long> {


    /**
     * Finds user by email address
     * @param email email address to look for
     * @return Optional with or without a UserEntity present, depending on whether a user was found
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Finds user by screenName
     * @param screenName screenName to look for
     * @return See findByEmail
     */
    Optional<UserEntity> findByScreenName(String screenName);

}
