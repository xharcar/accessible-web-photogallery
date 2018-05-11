package cz.muni.fi.accessiblewebphotogallery.web.validation;

import cz.muni.fi.accessiblewebphotogallery.web.pto.BuildingInfoPto;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class BuildingInfoPtoValidator implements Validator {

    public BuildingInfoPtoValidator(){
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return BuildingInfoPto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        BuildingInfoPto pto = (BuildingInfoPto) target;
        if(pto.getBuildingName().length() > 192){
            errors.rejectValue("buildingName","BuildingNameTooLong");
        }
        if(pto.getFocusText().length() > 384){
            errors.rejectValue("focusText","FocusTextTooLong");
        }
    }
}
