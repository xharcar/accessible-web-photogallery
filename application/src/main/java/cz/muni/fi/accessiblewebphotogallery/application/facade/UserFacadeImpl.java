package cz.muni.fi.accessiblewebphotogallery.application.facade;

import cz.muni.fi.accessiblewebphotogallery.application.service.iface.UserService;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.UserFacade;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.apache.commons.lang3.Validate;
import org.dozer.Mapper;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Transactional(readOnly = true)
    public Optional<UserDto> findById(Long id) {
        return userService.findById(id).map(this::userEntityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findByEmail(String email) {
        return userService.findByEmail(email).map(this::userEntityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findByLoginName(String loginName) {
        return userService.findByLoginName(loginName).map(this::userEntityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findByScreenNameContainingIgnoreCase(String partialScreenName) {
        return userService.findByScreenNameContainingIgnoreCase(partialScreenName).stream().map(this::userEntityToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll(){
        List<UserEntity> entityList = userService.findAll();
        return entityList.stream().map(this::userEntityToDto).collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(String identifier, String password) {
        Optional<UserEntity> byLogin = userService.findByLoginName(identifier);
        boolean rv = false;
        if(byLogin.isPresent()){
            rv = userService.authenticateUser(byLogin.get(),password);
        } else {
            Optional<UserEntity> byEmail = userService.findByEmail(identifier);
            if(byEmail.isPresent()){
                rv = userService.authenticateUser(byEmail.get(),password);
            }
        }
        return rv;
    }

    @Override
    @Transactional
    public Pair<UserDto, String> registerUser(UserDto user, String password) {
        Pair<UserDto,String> rv = null;
        Pair<UserEntity,String> prelim = userService.registerUser(userDtoToEntity(user),password);
        if (null != prelim){
            user.setId(prelim.getFirst().getId());
            user.setPassHash(prelim.getFirst().getPasswordHash());
            user.setPassSalt(prelim.getFirst().getPasswordSalt());
            rv = Pair.of(user,prelim.getSecond());
        }
        return rv;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAdmin(UserDto user) {
        return userService.isAdmin(userDtoToEntity(user));
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto user) {
        Validate.notNull(user.getId());
        UserEntity entity = userDtoToEntity(user);
        UserEntity updated = userService.updateUser(entity);
        return userEntityToDto(updated);
    }

    @Override
    @Transactional
    public boolean confirmUserRegistration(String email, String token) {
        return userService.confirmUser(email,token);
    }

    private UserDto userEntityToDto(UserEntity e){
        return mapper.map(e,UserDto.class);
    }

    private UserEntity userDtoToEntity(UserDto dto){
        return mapper.map(dto,UserEntity.class);
    }
}
