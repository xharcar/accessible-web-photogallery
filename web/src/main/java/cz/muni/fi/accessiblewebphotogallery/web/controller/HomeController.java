package cz.muni.fi.accessiblewebphotogallery.web.controller;

import cz.muni.fi.accessiblewebphotogallery.iface.ApplicationConfig;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.BuildingInfoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.AlbumFacade;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.BuildingInfoFacade;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.PhotoFacade;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.UserFacade;
import cz.muni.fi.accessiblewebphotogallery.web.AuthenticationProviderImpl;
import cz.muni.fi.accessiblewebphotogallery.web.PhotoGalleryFrontendMapper;
import cz.muni.fi.accessiblewebphotogallery.web.pto.PhotoPto;
import cz.muni.fi.accessiblewebphotogallery.web.pto.UserRegistrationPto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    @Inject
    private ApplicationConfig applicationConfig;

    private static final Logger log = LogManager.getLogger(HomeController.class);

    @InitBinder
    protected void initBinder(WebDataBinder binder){

    }

    @RequestMapping("/browse")
    public String browse(Model model, @RequestParam(name = "p", defaultValue = "1") Integer pageNr) {
        PageImpl<PhotoDto> photoPage = photoFacade.findNewestFirst(PageRequest.of(pageNr-1,10));
        PageImpl<PhotoPto> photoPtoPage = new PageImpl<>(photoPage.getContent().stream().map(PhotoGalleryFrontendMapper::photoDtoToPto).collect(Collectors.toList()), photoPage.getPageable(), photoPage.getTotalElements());
        List<String> base64Ids = new ArrayList<>();
        for(int i=0;i<photoPage.getContent().size();i++){
            base64Ids.add(photoPage.getContent().get(i).getBase64Id());
        }
        List<String> thumbnailPathList = new ArrayList<>();
        File photoDir = new File(applicationConfig.getPhotoDirectory());
        for(int i=0;i<base64Ids.size();i++){
            thumbnailPathList.add(photoDir.getAbsolutePath() + base64Ids.get(i) + "_thumb.jpg");
        }
        model.addAttribute("photoPtoPage",photoPtoPage);
        model.addAttribute("thumbnails",thumbnailPathList);
        return "browse";
    }

    @RequestMapping("/detail/{base64}")
    public String photoDetail(@PathVariable("base64") String base64Id, Model model) throws Exception {
        Optional<PhotoDto> photoDtoOpt = photoFacade.findByBase64Id(base64Id);
        if(!photoDtoOpt.isPresent()) {
            throw new IllegalArgumentException("Photo with Base-64 ID " + base64Id + " not found.");
        }
        File photoDir = new File(applicationConfig.getPhotoDirectory());
        File[] fileList = photoDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                // photos can be JPEGs, TIFFs, PNGs for all we know- so the filter has to be extension-insensitive
                return name.startsWith(base64Id) && !name.endsWith(".json");
            }
        });
        if(fileList == null){
            throw new NullPointerException("Photo File instance couldn't be created - file list is null.");
        }
//        if(fileList.length != 1){
//            throw new Exception("File list supposed to contain just photo file contains more, not supposed to happen.");
//        } // might use this for extended error control if necessary but doesn't seem so at the moment
        String photoFilePath = fileList[0].getCanonicalPath();
        List<BuildingInfoDto> buildingDtos = buildingFacade.findByPhoto(photoDtoOpt.get());
        Optional<PhotoDto> nextPhotoChronoOpt = photoFacade.findById(photoDtoOpt.get().getId()+1);
        Optional<PhotoDto> prevPhotoChronoOpt = photoFacade.findById(photoDtoOpt.get().getId()-1);
        // TODO:
        // if next photo chronologically is present, put up a link to it
        // same with previous
        model.addAttribute("photoFilePath",photoFilePath);
        model.addAttribute("photoPto",PhotoGalleryFrontendMapper.photoDtoToPto(photoDtoOpt.get()));
        model.addAttribute("buildingPtos",buildingDtos.stream().map(PhotoGalleryFrontendMapper::buildingDtoToPto));
        return "detail";
    }


    @RequestMapping("/album/{base64Album}/{albumIndex}/{base64Photo}")
    public String photoDetailInAlbum(@PathVariable("base64Photo") String photoBase64Id, @PathVariable("base64Album") String albumBase64Id, @PathVariable("albumIndex") Integer albumIndex, Model model){



        //(idea:) model has photoFilePath,photoPto and buildingPtos, same as photoDetail,
        // but instead of linking to previous and next photos chronologically, links to prev/next in album cyclically
        return "detail_album";
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
