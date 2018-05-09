package cz.muni.fi.accessiblewebphotogallery.iface.facade;

import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Optional;

public interface UserFacade {

    List<UserDto> findAll();

    Optional<UserDto> findById(Long id);

    Optional<UserDto> findByEmail(String email);

    Optional<UserDto> findByLoginName(String loginName);

    Optional<UserDto> findByIdentifier(String identifier);

    List<UserDto> findByScreenNameApx(String apxName);

    boolean isAdmin(UserDto user);

    Pair<UserDto,String> registerUser(UserDto user, String password);

    // to be used with both email and login name
    Pair<Boolean,Optional<UserDto>> authenticateUser(String identifier, String password);

    UserDto updateUser(UserDto user);

    boolean confirmUserRegistration(String email, String token);

    void deleteUser(UserDto user);

}
