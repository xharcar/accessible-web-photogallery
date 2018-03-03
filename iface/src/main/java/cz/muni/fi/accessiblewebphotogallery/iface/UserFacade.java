package cz.muni.fi.accessiblewebphotogallery.iface;

import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;

import java.util.Optional;

public interface UserFacade {

    Optional<UserDto> findById(Long id);

    Optional<UserDto> findByEmail(String email);

    Optional<UserDto> findByScreenName(String screenName);

}
