package cz.muni.fi.accessiblewebphotogallery.iface.facade;

import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Optional;

public interface UserFacade {

    // findBy* methods: map 1:1 to DAO methods

    Optional<UserDto> findById(Long id);

    Optional<UserDto> findByEmail(String email);

    Optional<UserDto> findByLoginName(String loginName);

    List<UserDto> findByScreenNameContainingIgnoreCase(String partialScreenName);

    List<UserDto> findAll();

    // to be used with both email and login name
    boolean authenticate(String identifier, String password);

    Pair<UserDto,String> registerUser(UserDto user, String password);

    boolean isAdmin(UserDto user);

    UserDto updateUser(UserDto user);

    boolean confirmUserRegistration(String email, String token);

}
