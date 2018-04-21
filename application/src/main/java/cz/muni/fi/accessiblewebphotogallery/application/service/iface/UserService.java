package cz.muni.fi.accessiblewebphotogallery.application.service.iface;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Optional;

public interface UserService extends PagedService<UserEntity>{

    @Override
    PageImpl<UserEntity> findAll(Pageable pageable);

    Optional<UserEntity> findById(Long id);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByLoginName(String loginName);

    List<UserEntity> findByScreenNameContainingIgnoreCase(String insensitiveScreenNamePart);

    boolean isAdmin(UserEntity user);

    Pair<UserEntity,String> registerUser(UserEntity user, String password);

    boolean authenticateUser(UserEntity user, String password);

    UserEntity updateUser(UserEntity user);

    boolean confirmUser(String email, String token);

}
