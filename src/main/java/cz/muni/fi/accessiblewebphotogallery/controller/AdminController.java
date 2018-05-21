package cz.muni.fi.accessiblewebphotogallery.controller;

import cz.muni.fi.accessiblewebphotogallery.PhotoGalleryFrontendMapper;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.facade.facade.PhotoFacade;
import cz.muni.fi.accessiblewebphotogallery.proxy.PhotoProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private PhotoFacade photoFacade;
    private static final Logger log = LogManager.getLogger(AdminController.class);

    @Autowired
    public AdminController(PhotoFacade pf){
        photoFacade = pf;
    }

    @RequestMapping(value = "/edit/{photoId}", method = RequestMethod.GET)
    public String editPhotoGet(@PathVariable("photoId") String photoId, Model model){
        Optional<PhotoDto> photoDtoOptional = photoFacade.findPhotoById(photoId);
        if(!photoDtoOptional.isPresent()){
            log.error("Photo with ID " + photoId + " is supposed to exist but doesn't");
            return "/error";
        }
        model.addAttribute("photoProxy",PhotoGalleryFrontendMapper.photoDtoToProxy(photoDtoOptional.get()));
        model.addAttribute("backLink","/detail?photo=" + photoDtoOptional.get().getId());
        return "/admin/photo/edit";
    }

    @RequestMapping(value = "/edit/{photoId}", method = RequestMethod.POST)
    public String editPhotoPost(@PathVariable("photoId") String photoId,
                                @Valid @ModelAttribute("photoProxy")PhotoProxy photoProxy, Model model){
        if(!photoId.equals(photoProxy.getId())){ // should not happen
            log.error("ID of photo in model is different from the one in the URL.");
            return "/error";
        }
        Optional<PhotoDto> photoDtoOptional = photoFacade.findPhotoById(photoId);
        if(!photoDtoOptional.isPresent()){ // should not happen
            log.error("Attempting to edit a photo with ID that doesn't seem to exist.");
            return "/error";
        }
        PhotoDto dto = PhotoGalleryFrontendMapper.photoProxyToDto(photoProxy);
        photoFacade.updatePhoto(dto);
        return "redirect:/detail?photo="+photoId;
    }
}
