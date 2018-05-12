package cz.muni.fi.accessiblewebphotogallery.web.controller;

import cz.muni.fi.accessiblewebphotogallery.application.ApplicationConfig;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.AlbumDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.BuildingInfoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.AlbumFacade;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.BuildingInfoFacade;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.PhotoFacade;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.UserFacade;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;
import cz.muni.fi.accessiblewebphotogallery.web.AuthenticationProviderImpl;
import cz.muni.fi.accessiblewebphotogallery.web.Mailer;
import cz.muni.fi.accessiblewebphotogallery.web.PhotoGalleryFrontendMapper;
import cz.muni.fi.accessiblewebphotogallery.web.pto.AlbumPto;
import cz.muni.fi.accessiblewebphotogallery.web.pto.BuildingInfoPto;
import cz.muni.fi.accessiblewebphotogallery.web.pto.PhotoPto;
import cz.muni.fi.accessiblewebphotogallery.web.pto.UserRegistrationPto;
import cz.muni.fi.accessiblewebphotogallery.web.validation.AlbumPtoValidator;
import cz.muni.fi.accessiblewebphotogallery.web.validation.BuildingInfoPtoValidator;
import cz.muni.fi.accessiblewebphotogallery.web.validation.PhotoPtoValidator;
import cz.muni.fi.accessiblewebphotogallery.web.validation.UserRegistrationPtoValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
        if (binder.getTarget() instanceof UserRegistrationPto) {
            binder.addValidators(new UserRegistrationPtoValidator(userFacade));
        }
        if(binder.getTarget() instanceof  PhotoPto){
            binder.addValidators(new PhotoPtoValidator());
        }
        if(binder.getTarget() instanceof BuildingInfoPto){
            binder.addValidators(new BuildingInfoPtoValidator());
        }
        if(binder.getTarget() instanceof AlbumPto){
            binder.addValidators(new AlbumPtoValidator());
        }
    }

    @RequestMapping("/")
    public String index(){return "redirect:/browse";} // auto-redirect to the most recent


    @RequestMapping("/")
    public String hello(Model model){
        model.addAttribute("msg","UROD BLYAT");
        return "hello";
    }

    @RequestMapping("/browse")
    public String browse(Model model, @RequestParam(name = "page", defaultValue = "1") Integer pageNr) {
        PageImpl<PhotoDto> photoDtoPage = photoFacade.findNewestFirst(PageRequest.of(pageNr-1,10));
        PageImpl<PhotoPto> photoPtoPage = new PageImpl<>(photoDtoPage.getContent().stream().map(PhotoGalleryFrontendMapper::photoDtoToPto).collect(Collectors.toList()), photoDtoPage.getPageable(), photoDtoPage.getTotalElements());
        List<String> base64Ids = new ArrayList<>();
        for(int i=0;i<photoDtoPage.getContent().size();i++){
            base64Ids.add(photoDtoPage.getContent().get(i).getBase64Id());
        }
        List<String> thumbnailPathList = new ArrayList<>();
        File photoDir = new File(applicationConfig.getPhotoDirectory());
        for(int i=0;i<photoDtoPage.getSize();i++){
            thumbnailPathList.add(photoDir.getAbsolutePath() + File.separator +  photoDtoPage.getContent().get(i).getBase64Id() + "_thumb.jpg");
        }
        model.addAttribute("photos",photoPtoPage);
        model.addAttribute("thumbnails",thumbnailPathList);
        return "browse";
    }

    @RequestMapping("/browse/user/{userId}")
    public String browseByUser(Model model, @PathVariable("userId") Long userId,@RequestParam(name = "page", defaultValue = "1") Integer pageNr){
        Optional<UserDto> uploaderOpt = userFacade.findById(userId);
        if(!uploaderOpt.isPresent()){
            throw new IllegalStateException("User with ID " + userId + " supposed to exist but not found.");
        }
        PageRequest pageRq = PageRequest.of(pageNr-1,10);
        PageImpl<PhotoDto> photoDtoPage = photoFacade.findByUploader(uploaderOpt.get(),pageRq);
        PageImpl<PhotoPto> photoPtoPage = new PageImpl<>(photoDtoPage.getContent().stream().map(PhotoGalleryFrontendMapper::photoDtoToPto).collect(Collectors.toList()),pageRq,photoDtoPage.getTotalElements());
        List<String> thumbnailPathList = new ArrayList<>();
        File photoDir = new File(applicationConfig.getPhotoDirectory());
        for (int i = 0; i < photoDtoPage.getSize(); i++) {
            thumbnailPathList.add(photoDir.getAbsolutePath() + File.separator + photoDtoPage.getContent().get(i).getBase64Id() + "_thumb.jpg");
        }
        model.addAttribute("photos", photoPtoPage);
        model.addAttribute("thumbnails",thumbnailPathList);
        return "browse";
    }

    @RequestMapping("/detail/{base64}")
    public String photoDetail(@PathVariable("base64") String base64Id, Model model){
        Optional<PhotoDto> photoDtoOpt = photoFacade.findByBase64Id(base64Id);
        if(!photoDtoOpt.isPresent()) {
            throw new IllegalArgumentException("Photo with Base-64 ID " + base64Id + " not found.");
        }
        File photoDir = new File(applicationConfig.getPhotoDirectory());
        File[] fileList = photoDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                // photos can be JPEGs, TIFFs, PNGs for all we know- so the filter has to be extension-insensitive
                return name.startsWith(base64Id) && !name.contains("thumb") && !name.endsWith(".json");
            }
        });
        if(fileList == null){
            throw new NullPointerException("Photo File instance couldn't be created - file list is null.");
        }
//        if(fileList.length != 1){
//            throw new Exception("File list supposed to contain just photo file contains more, not supposed to happen.");
//        } // might use this for extended error control if necessary but doesn't seem so at the moment
        String photoFilePath = fileList[0].getAbsolutePath();
        List<BuildingInfoDto> buildingDtos = buildingFacade.findByPhoto(photoDtoOpt.get());
        Optional<PhotoDto> nextPhotoChronoOpt = photoFacade.findById(photoDtoOpt.get().getId()+1);
        Optional<PhotoDto> prevPhotoChronoOpt = photoFacade.findById(photoDtoOpt.get().getId()-1);
        if(!nextPhotoChronoOpt.isPresent()){
            throw new IllegalStateException("Next chronological photo entry does not exist.");
            // may not work, need to check
        }
        model.addAttribute("nextPhotoLink","/detail/" + nextPhotoChronoOpt.get().getBase64Id());
        if(!prevPhotoChronoOpt.isPresent()){
            throw new IllegalStateException("Previous chronological photo entry does not exist.");
        }
        model.addAttribute("prevPhotoLink", "/detail/" + prevPhotoChronoOpt.get().getBase64Id());
        model.addAttribute("photoFilePath",photoFilePath);
        model.addAttribute("photoPto",PhotoGalleryFrontendMapper.photoDtoToPto(photoDtoOpt.get()));
        model.addAttribute("buildingPtos",buildingDtos.stream().map(PhotoGalleryFrontendMapper::buildingDtoToPto));
        if(isAllowedToEdit(photoDtoOpt.get())){
            model.addAttribute("editPhotoLink","/edit/"+base64Id);
        }else{
            model.addAttribute("editPhotoLink", null);
        }
        String loggedInUserIdent = AuthenticationProviderImpl.getLoggedUserLoginId();
        if(loggedInUserIdent != null){
            Optional<UserDto> loggedInUserOpt = userFacade.findByIdentifier(loggedInUserIdent);
            if(!loggedInUserOpt.isPresent()){
                throw new IllegalStateException("User with identifier:" + loggedInUserIdent + " not found.");
            }
            model.addAttribute("userAlbums",albumFacade.findByAlbumOwner(loggedInUserOpt.get()));
        }
        return "detail";
    }


    @RequestMapping("/album/{base64Album}/{albumIndex}/{base64Photo}")
    public String photoDetailInAlbum(@PathVariable("base64Photo") String photoBase64Id, @PathVariable("base64Album") String albumBase64Id, @PathVariable("albumIndex") Integer albumIndex, Model model){
        Optional<PhotoDto> photoDtoOpt = photoFacade.findByBase64Id(photoBase64Id);
        if(!photoDtoOpt.isPresent()) {
            throw new IllegalArgumentException("Photo with Base-64 ID " + photoBase64Id + " not found.");
        }
        File photoDir = new File(applicationConfig.getPhotoDirectory());
        File[] fileList = photoDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                // photos can be JPEGs, TIFFs, PNGs for all we know- so the filter has to be extension-insensitive except for not accepting JSONs
                return name.startsWith(photoBase64Id) && !name.contains("thumb") && !name.endsWith(".json");
            }
        });
        if(fileList == null){
            throw new NullPointerException("Photo File instance couldn't be created - file list is null.");
        }
        Optional<AlbumDto> albumDtoOpt = albumFacade.findByBase64Id(albumBase64Id);
        if(!albumDtoOpt.isPresent()){
            throw new IllegalArgumentException("Album with base-64 ID " + albumBase64Id + " not found.");
        }
        List<String> albumPhotoList = albumFacade.listPhotosInAlbum(albumDtoOpt.get());
        int currentIndex = albumPhotoList.indexOf(photoBase64Id);
        if(albumIndex != currentIndex){
            throw new IllegalArgumentException("Album photo index and photoDetailInAlbum album index parameter don't match up");
        }
        int prevIndex, nextIndex;
        if(albumIndex == 0){
            prevIndex = albumPhotoList.size()-1;
        }else{
            prevIndex = albumIndex-1;
        }
        if(albumIndex == albumPhotoList.size()){
            nextIndex = 0;
        }else{
            nextIndex = albumIndex+1;
        }
        List<BuildingInfoPto> buildingInfoPtos = buildingFacade.findByPhoto(photoDtoOpt.get()).stream().map(PhotoGalleryFrontendMapper::buildingDtoToPto).collect(Collectors.toList());
        String albumPrevLink = "/album/" + albumBase64Id + "/" + prevIndex + "/" + albumPhotoList.get(prevIndex);
        String albumNextLink = "/album/" + albumBase64Id + "/" + nextIndex + "/" + albumPhotoList.get(nextIndex);
        model.addAttribute("photoPath",fileList[0].getAbsolutePath());
        model.addAttribute("albumPrevLink", albumPrevLink);
        model.addAttribute("albumNextLink", albumNextLink);
        model.addAttribute("photoPto",PhotoGalleryFrontendMapper.photoDtoToPto(photoDtoOpt.get()));
        model.addAttribute("buildingPtos",buildingInfoPtos);
        if(isAllowedToEdit(photoDtoOpt.get())){
            model.addAttribute("editPhotoLink","/edit/"+photoBase64Id);
        }else{
            model.addAttribute("editPhotoLink", null);
        }
        return "detail_album";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String registrationGet(Model model) {
        if (!model.containsAttribute("userRegistrationPto")) {
            model.addAttribute("userRegistrationPto", new UserRegistrationPto());
        }
        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registrationPost(@Valid UserRegistrationPto userRegistrationPto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        UserDto userDto = PhotoGalleryFrontendMapper.userRegistrationPtoToDto(userRegistrationPto);
        userDto.setAccountState(AccountState.INACTIVE);
        Pair<UserDto, String> registerResult = userFacade.registerUser(userDto, userRegistrationPto.getPassword());
        String confirmLink = "www.photogallery.com/confirm?email=" /*to be modified as appropriate*/
                + registerResult.getFirst().getEmail() + "&token=" + registerResult.getSecond();
        boolean mail_ok = Mailer.sendConfirmRegistrationMail(userDto.getEmail(),confirmLink);
        if(!mail_ok){
            userFacade.deleteUser(registerResult.getFirst());
            model.addAttribute("failureMessage","Registration failed. Please try again later.");
            return "registration";
        }
        AuthenticationProviderImpl.logInUser(userDto, userRegistrationPto.getPassword());
        // user can be logged in after successful registration, that's not a problem
        return "registration_successful";
    }

    @RequestMapping(value = "/edit/{base64}", method = RequestMethod.GET)
    public String editPhotoGet(@PathVariable String base64, Model model){
        Optional<PhotoDto> photoOpt = photoFacade.findByBase64Id(base64);
        if(!photoOpt.isPresent()){
            log.error("Failed to retrieve photo entry with base64 ID: " + base64+ ", that is supposed to exist.");
            return "redirect:/browse?page=1"; // or somewhere else, I don't know...
            // (well, the photo entry doesn't seem to exist, better go somewhere valid, I guess...)
        }
        List<BuildingInfoDto> buildingInfoDtoList = buildingFacade.findByPhoto(photoOpt.get());
        model.addAttribute("photoPto", PhotoGalleryFrontendMapper.photoDtoToPto(photoOpt.get()));
        model.addAttribute("buildingList",buildingInfoDtoList.stream().map(PhotoGalleryFrontendMapper::buildingDtoToPto).collect(Collectors.toList()));
        model.addAttribute("selfLink","/detail/"+base64); //for a 'clear photo' button, which then redirects back to the detail of this photo
        return "edit";
    }


    @RequestMapping(value = "/edit/{base64}", method = RequestMethod.POST)
    public String editPhotoPost(@PathVariable String base64, @Valid PhotoPto photoPto, @Valid List<BuildingInfoPto> buildingList, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("failureMessage","Invalid input.");
            return "/edit/"+base64;
        }
        Optional<UserDto> uploaderOpt = userFacade.findById(photoPto.getUploader().getId());
        if(!uploaderOpt.isPresent()){
            throw new IllegalStateException("Uploader for photo with base64 ID: " + base64 + " not found.");
        }
        Optional<PhotoDto> photoDtoOpt = photoFacade.findByBase64Id(base64);
        if(!photoDtoOpt.isPresent()){
            throw new IllegalStateException("Photo with base64 ID: " + base64 + "not found.");
        }
        List<BuildingInfoDto> updatedList = new ArrayList<>();
        for(BuildingInfoPto x:buildingList){
            updatedList.add(PhotoGalleryFrontendMapper.buildingPtoToDto(x,photoDtoOpt.get()));
        }
        buildingFacade.updateBuildings(updatedList);
        photoFacade.updatePhoto(PhotoGalleryFrontendMapper.photoPtoToDto(photoPto,uploaderOpt.get()));
        // It makes sense that a photo can be edited on the same page as the buildings in it
        return "redirect:/detail/" + base64;
    }


    @RequestMapping(value = "/confirm")
    public String confirmUserRegistration(@RequestParam String email, @RequestParam String token, Model model){
        boolean success = userFacade.confirmUserRegistration(email, token);
        model.addAttribute("success", success ? "success" : "failure");
        return "redirect:/profile";
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

    private boolean isAllowedToEdit(PhotoDto photo){
        String loginId = AuthenticationProviderImpl.getLoggedUserLoginId();
        if(loginId == null) return false;
        Optional<UserDto> userDtoOpt = userFacade.findByIdentifier(loginId);
        return userDtoOpt.isPresent() && (userDtoOpt.get().getAccountState().equals(AccountState.ADMINISTRATOR) || userDtoOpt.get().equals(photo.getUploader()));
    }

    @RequestMapping("/login")
    public String login(@RequestParam(required = false, defaultValue = "false") Boolean logout, Model model) {
        model.addAttribute("logout", logout);
        return "login";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        AuthenticationProviderImpl.logout(request);
        return "redirect:/login?logout=true";
    }

}
