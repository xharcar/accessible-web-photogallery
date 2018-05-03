package cz.muni.fi.accessiblewebphotogallery.web.controller;

import cz.muni.fi.accessiblewebphotogallery.iface.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.AlbumFacade;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.BuildingInfoFacade;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.PhotoFacade;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.UserFacade;
import cz.muni.fi.accessiblewebphotogallery.web.AuthenticationProviderImpl;
import cz.muni.fi.accessiblewebphotogallery.web.PhotoGalleryFrontendMapper;
import cz.muni.fi.accessiblewebphotogallery.web.pto.PhotoPto;
import cz.muni.fi.accessiblewebphotogallery.web.pto.UserRegistrationPto;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.stream.Collectors;

@Controller
@RequestMapping("")
public class HomeController {

    @Inject
    private UserFacade userFacade;
    @Inject
    private PhotoFacade photoFacade;
    @Inject
    private BuildingInfoFacade buildingFacade;
    @Inject
    private AlbumFacade albumFacade;

    @InitBinder
    protected void initBinder(WebDataBinder binder){

    }

    @RequestMapping("/browse")
    public String view(Model model, @RequestParam(name = "p", defaultValue = "1") Integer pageNr){
        PageImpl<PhotoDto> photoPage = photoFacade.findNewestFirst(PageRequest.of(pageNr-1,10));
        PageImpl<PhotoPto> photoPtoPage = new PageImpl<PhotoPto>(photoPage.getContent().stream().map(PhotoGalleryFrontendMapper::photoDtoToPto).collect(Collectors.toList()),photoPage.getPageable(),photoPage.getTotalElements());
        model.addAttribute("photos",photoPtoPage);
        return "browse";
    }

    @RequestMapping("/browse?p={base64}")
    public String photoDetail(@PathVariable("base64") String base64Id, Model model){
        // TBD
        return "";
    }


    @RequestMapping("/browse?p={base64Photo}&album={base64Album}&index={albumIndex}")
    public String photoDetailInAlbum(@PathVariable("base64Photo") String photoBase64Id, @PathVariable("base64Album") String albumBase64Id, @PathVariable("albumIndex") Integer albumIndex){
        return "";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registrationGet(Model model) {
        if (!model.containsAttribute("userRegistrationPto")) {
            model.addAttribute("userRegistrationPto", new UserRegistrationPto());
        }
        return "registration";
    }


    @RequestMapping("/about")
    public String about() {
        return "about";
    }

    @RequestMapping("/user/profile")
    public String userProfile(Model model) {
        String loginId = AuthenticationProviderImpl.getLoggedUserLoginId();
        model.addAttribute("userOptional",userFacade.findByIdentifier(loginId).map(PhotoGalleryFrontendMapper::userDtoToPto));
        return "/profile";
    }

}
