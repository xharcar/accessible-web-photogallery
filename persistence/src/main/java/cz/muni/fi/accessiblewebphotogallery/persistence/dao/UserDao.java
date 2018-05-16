package cz.muni.fi.accessiblewebphotogallery.persistence.dao;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<UserEntity, Long> {


    /**
     * Finds user by email address
     *
     * @param email email address to look for
     * @return Optional with or without a UserEntity present, depending on whether a user was found
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Finds user by login name - for checking whether a login name exists at account creation
     *
     * @param loginName loginName to look for
     * @return see findByEmail
     */
    Optional<UserEntity> findByLoginName(String loginName);

    /**
     * General lookup by screen name- case insensitive
     *
     * @param insensitiveScreenNamePart (partial) case-insensitive screen name to look up
     * @return analogic to findByScreenNameContaning (ignoring case)
     */
    List<UserEntity> findByScreenNameContainingIgnoreCase(String insensitiveScreenNamePart);


}
