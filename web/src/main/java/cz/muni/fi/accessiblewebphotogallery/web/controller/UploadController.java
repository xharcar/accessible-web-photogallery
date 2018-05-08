package cz.muni.fi.accessiblewebphotogallery.web.controller;


import cz.muni.fi.accessiblewebphotogallery.iface.ApplicationConfig;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.BuildingInfoFacade;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.PhotoFacade;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.UserFacade;
import cz.muni.fi.accessiblewebphotogallery.web.AuthenticationProviderImpl;
import cz.muni.fi.accessiblewebphotogallery.web.PhotoPtoValidator;
import cz.muni.fi.accessiblewebphotogallery.web.pto.PhotoPto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Controller
@RequestMapping("/upload")
@MultipartConfig
public class UploadController {
// upload directory = applicationConfig.photoDirectory
    @Inject
    private PhotoFacade photoFacade;

    @Inject
    private BuildingInfoFacade buildingFacade;

    @Inject
    private UserFacade userFacade;

    @Inject
    private ApplicationConfig applicationConfig;

    private final Logger log = LogManager.getLogger(this);

    @InitBinder
    protected void initBinder(WebDataBinder binder){
        if(binder.getTarget() instanceof PhotoPto){
            binder.addValidators(new PhotoPtoValidator(photoFacade));
        }
    }

    @RequestMapping("/form")
    public String uploadForm(Model model){
        PhotoPto photoPto = new PhotoPto();
        model.addAttribute("photoPto",photoPto);
        return "/upload/form";
    }

    @RequestMapping(value = "/form",method = RequestMethod.POST)
    public String uploadFormPost(@ModelAttribute @Valid PhotoPto photoPto, BindingResult bindingResult,
                                 HttpServletRequest request){
        if(!bindingResult.hasErrors()){
            Optional<UserDto> uploader = userFacade.findByIdentifier(AuthenticationProviderImpl.getLoggedUserLoginId());
            if(!uploader.isPresent()){
                throw new IllegalStateException("Logged in user not found in DB.");
            }
            Instant photoUploadTime = Instant.now();
            Part photoFilePart;
            try {
                photoFilePart = request.getPart("photoFile");
            } catch (IOException | ServletException e) {
                log.catching(e);
                log.error("Exception retrieving uploaded photo file request part.");
                return "/upload/form";
            }
            String photoOrigFileName = photoFilePart.getSubmittedFileName();
            if(photoOrigFileName == null){
                log.error("Could not retrieve uploaded file's name (Possibly, none was uploaded).");
                return "/upload/form";
            }
            String[] photoFNSplit = photoOrigFileName.split(".");
            String ext = photoFNSplit[photoFNSplit.length-1];
            String photoFNFinal = applicationConfig.getPhotoDirectory() + File.separator + uploader.get().getLoginName() + "_" + photoUploadTime.getEpochSecond() + "." + ext;
            // so, eg. /home/photogallery/photos/xharcar_2147483647.jpg
            try {
                photoFilePart.write(photoFNFinal);
            } catch (IOException e) {
                log.catching(e);
                log.error("Error writing uploaded photo file to location: " + photoFNFinal+ "." + e.getMessage());
                return "/upload/form";
            }
            Part metadataFilePart;
            try {
                metadataFilePart = request.getPart("metadataFile");
            } catch (IOException | ServletException e) {
                log.catching(e);
                log.error("Exception retrieving metadata file request part.");
                return "/upload/form";
            }
            String origMDFileName = metadataFilePart.getSubmittedFileName();
            if(origMDFileName == null){
                return "redirect:/detail/" + createPhotoDBEntry(photoPto, new File(photoFNFinal), null);
            }
            String mdFNFinal = applicationConfig.getPhotoDirectory() + File.separator + uploader.get().getLoginName() + "_" + photoUploadTime.getEpochSecond() + ".json";
            try {
                photoFilePart.write(mdFNFinal);
            } catch (IOException e) {
                log.catching(e);
                log.error("IOException writing metadata file to location: " + mdFNFinal + "." + e.getMessage());
                return "/upload/form";
            } // alternatively, could just save w/o metadata...
            return "redirect:/detail/" + createPhotoDBEntry(photoPto, new File(photoFNFinal),new File(mdFNFinal));
        }
        return "/upload/form";
    }

    private String createPhotoDBEntry(PhotoPto photoPto, File photoFile, File metadataFile){
        Optional<UserDto> uploader = userFacade.findByIdentifier(AuthenticationProviderImpl.getLoggedUserLoginId());
        if(!uploader.isPresent()){
            throw new IllegalStateException("Logged in user not found in DB.");
        }
        PhotoDto photoDto = new PhotoDto();
        photoDto.setTitle(photoPto.getTitle());
        photoDto.setDescription(photoPto.getDescription());
        photoDto.setUploader(uploader.get());
        return photoFacade.registerPhoto(photoDto,photoFile,metadataFile).getBase64Id();
    }

}
