package cz.muni.fi.accessiblewebphotogallery.application.facade;

import cz.muni.fi.accessiblewebphotogallery.application.PhotoGalleryBackendMapper;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.UserService;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.UserFacade;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.apache.commons.lang3.Validate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserFacadeImpl implements UserFacade{

    private UserService userService;

    @Inject
    public UserFacadeImpl(UserService userService){
        this.userService = userService;
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
    public Optional<UserDto> findByIdentifier(String identifier) {
        Optional<UserEntity> searchResult;
        if(identifier.contains("@")){
            searchResult = userService.findByEmail(identifier);
        }else{
            searchResult = userService.findByLoginName(identifier);
        }
        return searchResult.map(this::userEntityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findByScreenNameApx(String apxName) {
        return userService.findByScreenNameApx(apxName).stream().map(this::userEntityToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll(){
        List<UserEntity> entityList = userService.findAll();
        return entityList.stream().map(this::userEntityToDto).collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public Pair<Boolean, Optional<UserDto>> authenticateUser(String identifier, String password) {
        Pair<Boolean,Optional<UserEntity>> toMap = userService.authenticateUser(identifier,password);
        return Pair.of(toMap.getFirst(),toMap.getSecond().map(this::userEntityToDto));
    }

    @Override
    @Transactional
    public Pair<UserDto, String> registerUser(UserDto user, String password) {
        Pair<UserDto,String> rv = null;
        Pair<UserEntity,String> prelim = userService.registerUser(userDtoToEntity(user),password);
        if (prelim != null){
            user.setId(prelim.getFirst().getId());
            user.setPasswordHash(prelim.getFirst().getPasswordHash());
            user.setPasswordSalt(prelim.getFirst().getPasswordSalt());
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
        return userService.confirmUserRegistration(email,token);
    }

    private UserDto userEntityToDto(UserEntity e){
        return PhotoGalleryBackendMapper.userEntityToDto(e);
    }

    private UserEntity userDtoToEntity(UserDto dto){
        return PhotoGalleryBackendMapper.userDtoToEntity(dto);
    }
}
