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
        if(urpto.getEmail() == null || urpto.getEmail().isEmpty()){
            errors.rejectValue("email","EmailMustNotBeEmpty");
        }
        if(urpto.getLoginName() == null || urpto.getLoginName().isEmpty()){
            errors.rejectValue("loginName","LoginNameMustNotBeEmpty");
        }
        if(urpto.getLoginName().length() > 64){
            errors.rejectValue("loginName","LoginNameTooLong");
        }
        if(urpto.getScreenName().length() > 128){
            errors.rejectValue("screenName","ScreenNameTooLong");
        }
        Optional<UserDto> oudto = userFacade.findByEmail(urpto.getEmail());
        if(oudto.isPresent()){
            errors.rejectValue("email","UserEmailAlreadyExists");
        }
        oudto = userFacade.findByLoginName(urpto.getLoginName());
        if(oudto.isPresent()){
            errors.rejectValue("loginName","UserLoginAlreadyExists");
        }
        if(urpto.getEmail().length() > 128){
            errors.rejectValue("email","EmailTooLong");
        }
    }
}
