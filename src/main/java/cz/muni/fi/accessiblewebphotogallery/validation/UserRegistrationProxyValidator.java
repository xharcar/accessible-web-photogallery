package cz.muni.fi.accessiblewebphotogallery.validation;

import cz.muni.fi.accessiblewebphotogallery.facade.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.facade.facade.UserFacade;
import cz.muni.fi.accessiblewebphotogallery.proxy.UserRegistrationProxy;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

public class UserRegistrationProxyValidator implements Validator {

    private UserFacade userFacade;

    public UserRegistrationProxyValidator(UserFacade userFacade){
        this.userFacade = userFacade;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistrationProxy.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserRegistrationProxy urp = (UserRegistrationProxy) target;
        Optional<UserDto> userDtoOptional = userFacade.findByEmail(urp.getEmail());
        if(userDtoOptional.isPresent()){
            errors.rejectValue("email","UserEmailAlreadyInUse","Email address already in use, choose another.");
        }
        userDtoOptional = userFacade.findByLoginName(urp.getLoginName());
        if(userDtoOptional.isPresent()){
            errors.rejectValue("loginName","UserLoginAlreadyInUse","User login already in use, please choose another.");
        }

    }
}
