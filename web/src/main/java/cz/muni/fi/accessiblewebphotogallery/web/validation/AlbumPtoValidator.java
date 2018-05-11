package cz.muni.fi.accessiblewebphotogallery.web.validation;

import cz.muni.fi.accessiblewebphotogallery.web.pto.AlbumPto;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class AlbumPtoValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return AlbumPto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AlbumPto pto = (AlbumPto) target;
        if(pto.getAlbumName().length() > 128){
            errors.rejectValue("albumName","AlbumNameTooLong");
        }
    }
}
