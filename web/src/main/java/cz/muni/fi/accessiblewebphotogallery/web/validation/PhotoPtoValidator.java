package cz.muni.fi.accessiblewebphotogallery.web.validation;

import cz.muni.fi.accessiblewebphotogallery.web.pto.PhotoPto;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PhotoPtoValidator implements Validator {

    public PhotoPtoValidator() {
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return PhotoPto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PhotoPto pto = (PhotoPto) target;
        if(pto.getTitle() == null || pto.getTitle().isEmpty()){
            errors.rejectValue("title","TitleMustNotBeEmpty");
        }
        if(pto.getTitle().length() > 128){
            errors.rejectValue("title","TitleTooLong");
        }
        if(pto.getDescription().length() > 2048){
            errors.rejectValue("description","DescriptionTooLong");
        }
    }
}