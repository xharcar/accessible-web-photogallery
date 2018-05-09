package cz.muni.fi.accessiblewebphotogallery.web.validation;

import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.UserFacade;
import cz.muni.fi.accessiblewebphotogallery.web.pto.UserRegistrationPto;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

public class UserRegistrationPtoValidator implements Validator {

    private UserFacade userFacade;

    public UserRegistrationPtoValidator(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserRegistrationPto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserRegistrationPto urpto = (UserRegistrationPto) target;
        Optional<UserDto> oudto = userFacade.findByEmail(urpto.getEmail());
        if(oudto.isPresent()){
            errors.rejectValue("email","UserEmailAlreadyExists");
        }
        oudto = userFacade.findByLoginName(urpto.getLoginName());
        if(oudto.isPresent()){
            errors.rejectValue("loginName","UserLoginAlreadyExists");
        }
    }
}
