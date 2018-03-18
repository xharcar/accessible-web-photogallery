package cz.muni.fi.accessiblewebphotogallery.application.facade;

import cz.muni.fi.accessiblewebphotogallery.application.service.iface.UserService;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.UserFacade;
import org.apache.commons.lang3.tuple.Pair;
import org.dozer.Mapper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Service
public class UserFacadeImpl implements UserFacade{

    private Mapper mapper;
    private UserService userService;

    @Inject
    public UserFacadeImpl(UserService userService, Mapper mapper){
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    public Optional<UserDto> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<UserDto> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<UserDto> findByLoginName(String loginName) {
        return Optional.empty();
    }

    @Override
    public List<UserDto> findByScreenNameContainingIgnoreCase(String partialScreenName) {
        return null;
    }

    @Override
    public boolean authenticate(String identifier, String password) {
        return false;
    }

    @Override
    public Pair<UserDto, String> registerUser(UserDto user, String password) {
        return null;
    }

    @Override
    public boolean isAdmin(UserDto user) {
        return false;
    }

    @Override
    public UserDto updateUser(UserDto user) {
        return null;
    }

    @Override
    public boolean confirmUserRegistration(String email, String token) {
        return false;
    }
}
