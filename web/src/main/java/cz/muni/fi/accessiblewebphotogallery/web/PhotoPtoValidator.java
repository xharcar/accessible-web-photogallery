package cz.muni.fi.accessiblewebphotogallery.web;

import cz.muni.fi.accessiblewebphotogallery.iface.facade.PhotoFacade;
import cz.muni.fi.accessiblewebphotogallery.web.pto.PhotoPto;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PhotoPtoValidator implements Validator {
    public PhotoPtoValidator(PhotoFacade photoFacade) {
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
    }
}
