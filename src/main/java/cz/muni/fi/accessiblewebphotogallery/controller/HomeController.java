package cz.muni.fi.accessiblewebphotogallery.controller;

import cz.muni.fi.accessiblewebphotogallery.AuthenticationProviderImpl;
import cz.muni.fi.accessiblewebphotogallery.Mailer;
import cz.muni.fi.accessiblewebphotogallery.PhotoGalleryFrontendMapper;
import cz.muni.fi.accessiblewebphotogallery.application.ApplicationConfig;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.facade.facade.AlbumFacade;
import cz.muni.fi.accessiblewebphotogallery.facade.facade.PhotoFacade;
import cz.muni.fi.accessiblewebphotogallery.facade.facade.UserFacade;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;
import cz.muni.fi.accessiblewebphotogallery.proxy.UserProxy;
import cz.muni.fi.accessiblewebphotogallery.proxy.UserRegistrationProxy;
import cz.muni.fi.accessiblewebphotogallery.validation.UserRegistrationProxyValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("")
public class HomeController {

    private UserFacade userFacade;
    private PhotoFacade photoFacade;
    private AlbumFacade albumFacade;
    private ApplicationConfig applicationConfig;
    private static final Logger log = LogManager.getLogger(HomeController.class);

    @Autowired
    public HomeController(UserFacade uf, PhotoFacade pf, AlbumFacade af, ApplicationConfig ac){
        userFacade = uf;
        photoFacade = pf;
        albumFacade = af;
        applicationConfig = ac;
    }


    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        if(binder.getTarget() instanceof UserRegistrationProxy){
            binder.addValidators(new UserRegistrationProxyValidator(userFacade));
        }
    }

    @RequestMapping({"/dummy"})
    public String dummy(Model model){
        model.addAttribute("msg","Sample Text");
        model.addAttribute("ttl","Minimum demo page");
        model.addAttribute("lenna", "https://upload.wikimedia.org/wikipedia/en/thumb/7/7d/Lenna_%28test_image%29.png/220px-Lenna_%28test_image%29.png");
        List<UserDto> userEntityList = userFacade.findAll();
        System.out.println("Users found: \n" + userEntityList);
        log.info("dummy method");
        model.addAttribute("users", userEntityList);
        model.addAttribute("loggedInUser",AuthenticationProviderImpl.getLoggedInUserIdentifier());
        return "/dummy";
    }

    @RequestMapping("/")
    public String index() {
        return "redirect:/dummy";
    } // TODO auto-redirect to the most recent when done


    @RequestMapping("/browse")
    public String browse(Model model, @RequestParam(name = "page", defaultValue = "1") Integer pageNr) {
        PageImpl<PhotoDto> photoDtoPage = photoFacade.findNewestPhotosFirst(PageRequest.of(pageNr - 1, 10));
        List<String> thumbnailPathList = new ArrayList<>();
        File photoDir = new File(applicationConfig.getPhotoDirectory());
        for (int i = 0; i < photoDtoPage.getContent().size(); i++) {
            thumbnailPathList.add(photoDir.getAbsolutePath() + File.separator + photoDtoPage.getContent().get(i).getId() + "_thumb.jpg");
        }
        if(photoDtoPage.getContent().size() > 0){
            model.addAttribute("browseMsg","Browsing photos, newest first.");
        }else{
            model.addAttribute("browseMsg","No photos available.");
        }
        model.addAttribute("photos", photoDtoPage.getContent().stream().map(PhotoGalleryFrontendMapper::photoDtoToProxy));
        model.addAttribute("thumbnails", thumbnailPathList);
        if(pageNr == 1){
            model.addAttribute("prevPageLink", "/browse?page=1");
        }else{
            model.addAttribute("prevPageLink","/browse?page=" + (pageNr-1));
        }
        if(pageNr == photoDtoPage.getTotalPages()){
            model.addAttribute("nextPageLink","/browse?page="+pageNr);
        }else{
            model.addAttribute("nextPageLink","/browse?page="+(pageNr+1));
        }
        return "/browse";
    }

    @RequestMapping("/user/{user}")
    public String browseByUser(Model model, @PathVariable(name = "user") String userLogin, @RequestParam(name = "page", defaultValue = "1") Integer pageNr) {
        Optional<UserDto> uploaderOpt = userFacade.findByLoginName(userLogin);
        if (!uploaderOpt.isPresent()) {
            log.error("User with login name " + userLogin + " supposed to exist but not found.");
            return "/error";
        }
        System.out.println(uploaderOpt.get());
        PageRequest pageRq = PageRequest.of(pageNr - 1, 10);
        PageImpl<PhotoDto> photoDtoPage = photoFacade.findPhotosByUploader(uploaderOpt.get(), pageRq);
        List<String> thumbnailPathList = new ArrayList<>();
        File photoDir = new File(applicationConfig.getPhotoDirectory());
        String userNick;
        if(!uploaderOpt.get().getScreenName().isEmpty()){
            userNick = uploaderOpt.get().getScreenName();
        }else{
            userNick = uploaderOpt.get().getLoginName();
        }
        if(photoDtoPage.getContent().size() == 0){
            model.addAttribute("browseMsg",userNick + " hasn't uploaded any photos.");
            return "/user";
        }else{
            model.addAttribute("browseMsg", "Browsing " + userNick + "'s photos.");
        }
        for (int i = 0; i < photoDtoPage.getContent().size(); i++) { // NOTE: use this for getting number of items on page, not page.getSize(), that's wrong
            thumbnailPathList.add(photoDir.getAbsolutePath() + File.separator + photoDtoPage.getContent().get(i).getId() + "_thumb.jpg");
        }
        model.addAttribute("photos", photoDtoPage.getContent());
        model.addAttribute("thumbnails", thumbnailPathList);
        if(pageNr == 1){
            model.addAttribute("prevPageNr", 1);
        }else{
            model.addAttribute("prevPageNr",pageNr-1);
        }
        if(pageNr == photoDtoPage.getTotalPages()){
            model.addAttribute("nextPageNr",pageNr);
        }else{
            model.addAttribute("nextPageNr",pageNr+1);
        }
        return "/user";
    }

//    @RequestMapping("/album")
//    public String browseAlbum(Model model, @RequestParam(name = "album") String albumId){
//        Optional<AlbumDto> albumDtoOptional = albumFacade.findAlbumById(albumId);
//        if(!albumDtoOptional.isPresent()){
//            log.error("Album with ID " + albumId + " not found.");
//            return "/error";
//        }
//        List<PhotoDto> photoDtoList = photoFacade.findPhotosInAlbum(albumDtoOptional.get());
//        List<String> thumbnailPathList = new ArrayList<>();
//        File photoDir = new File(applicationConfig.getPhotoDirectory());
//        for (int i = 0; i < photoDtoList.size(); i++) {
//            thumbnailPathList.add(photoDir.getAbsolutePath() + File.separator + photoDtoList.get(i).getId() + "_thumb.jpg");
//        }
//        if(photoDtoList.size() > 0){
//            model.addAttribute("browseMsg","Browsing photos, newest first.");
//        }else{
//            model.addAttribute("browseMsg","No photos available.");
//        }
//        model.addAttribute("photos", photoDtoList);
//        model.addAttribute("thumbnails", thumbnailPathList);
//        return "/browse";
//    }

    @RequestMapping("/detail")
    public String photoDetail(@RequestParam(name = "photo") String id, Model model) {
        Optional<PhotoDto> photoDtoOpt = photoFacade.findPhotoById(id);
        if (!photoDtoOpt.isPresent()) {
            log.info("Photo with ID " + id + " requested and not found.");
            return "/error";
        }
        File photoDir = new File(applicationConfig.getPhotoDirectory());
        File[] fileList = photoDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                // photos can be JPEGs, TIFFs, PNGs for all we know- so the filter has to be extension-insensitive
                return name.startsWith(id) && !name.contains("thumb") && !name.endsWith(".json");
            }
        });
        if (fileList == null || fileList.length == 0) { // a photo that's supposed to exist, but doesn't have a photo file...no
            return "/error";
        }
        String photoFilePath = fileList[0].getAbsolutePath();
        Optional<PhotoDto> nextPhotoChronoOpt = photoFacade.findNextUploadedPhoto(photoDtoOpt.get());
        Optional<PhotoDto> prevPhotoChronoOpt = photoFacade.findPreviousUploadedPhoto(photoDtoOpt.get());
        if (!nextPhotoChronoOpt.isPresent()) {
            model.addAttribute("nextLink", "/detail?photo=" + photoDtoOpt.get().getId());
        }else{
            model.addAttribute("nextLink", "/detail?photo=" + nextPhotoChronoOpt.get().getId());
        }
        if (!prevPhotoChronoOpt.isPresent()) {
            model.addAttribute("prevLink","/detail?photo=" + photoDtoOpt.get().getId());
        }else{
            model.addAttribute("prevLink","/detail?photo=" + prevPhotoChronoOpt.get().getId());
        }
        model.addAttribute("photoFilePath", photoFilePath);
        model.addAttribute("photoProxy", PhotoGalleryFrontendMapper.photoDtoToProxy(photoDtoOpt.get()));
        String loggedInUserIdent = AuthenticationProviderImpl.getLoggedInUserIdentifier();
        if (loggedInUserIdent != null) {
            Optional<UserDto> loggedInUserOpt = userFacade.findByIdentifier(loggedInUserIdent);
            if (!loggedInUserOpt.isPresent()) {
                throw new IllegalStateException("User with identifier:" + loggedInUserIdent + " not found.");
            }
            model.addAttribute("userAlbums", albumFacade.findAlbumsByOwner(loggedInUserOpt.get()));
        }
        return "/detail";
    }

//
//    @RequestMapping("/detail_album")
//    public String photoDetailInAlbum(@RequestParam("photo") String photoId, @RequestParam("album") String albumId, @RequestParam(name = "index", defaultValue = "1") Integer albumIndex, Model model) {
//        Integer realIndex = albumIndex - 1;
//        Optional<PhotoDto> photoDtoOpt = photoFacade.findPhotoById(photoId);
//        if (!photoDtoOpt.isPresent()) {
//            log.error("Photo with Base-64 ID " + photoId + " not found.");
//            return "/error";
//        }
//        File photoDir = new File(applicationConfig.getPhotoDirectory());
//        File[] fileList = photoDir.listFiles(new FilenameFilter() {
//            @Override
//            public boolean accept(File dir, String name) {
//                // photos can be JPEGs, TIFFs, PNGs for all we know- so the filter has to be extension-insensitive except for not accepting JSONs
//                return name.startsWith(photoId) && !name.contains("thumb") && !name.endsWith(".json");
//            }
//        });
//        if (fileList == null || fileList.length == 0) {
//            log.error("Photo with ID " + photoId + " found, but not its file.");
//            return "/error";
//        }
//        Optional<AlbumDto> albumDtoOpt = albumFacade.findAlbumById(albumId);
//        if (!albumDtoOpt.isPresent()) {
//            log.error("Album with ID " + albumId + " is supposed to exist but was not found.");
//            return "/error";
//        }
//        List<String> albumPhotoList = albumFacade.listPhotosInAlbum(albumDtoOpt.get());
//        int currentIndex = albumPhotoList.indexOf(photoId);
//        if (realIndex != currentIndex) {
//            log.error("Album photo index and photoDetailInAlbum album index parameter don't match up");
//            return "/error";
//        }
//        int prevIndex, nextIndex;
//        if (realIndex == 0) {
//            prevIndex = albumPhotoList.size();
//        } else {
//            prevIndex = realIndex;
//        }
//        if (realIndex == albumPhotoList.size() - 1 ) {
//            nextIndex = 1;
//        } else {
//            nextIndex = realIndex + 2;
//        }
//        String albumPrevLink = "/detail?photo=" + albumPhotoList.get(prevIndex) + "&album=" + albumId + "&index=" + prevIndex;
//        String albumNextLink = "/detail?photo=" + albumPhotoList.get(nextIndex) + "&album=" + albumId + "&index=" + nextIndex;
//        model.addAttribute("photoFilePath", fileList[0].getAbsolutePath());
//        model.addAttribute("prevLink", albumPrevLink);
//        model.addAttribute("nextLink", albumNextLink);
//        model.addAttribute("photo", PhotoGalleryFrontendMapper.photoDtoToProxy(photoDtoOpt.get()));
//        return "/detail";
//    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signupGet(Model model) {
        if (!model.containsAttribute("userRegistrationProxy")) {
            model.addAttribute("userRegistrationProxy", new UserRegistrationProxy());
        }
        return "/signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signupPost(@Valid UserRegistrationProxy userRegistrationPto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "/signup";
        }
        UserDto userDto = PhotoGalleryFrontendMapper.userRegistrationProxyToDto(userRegistrationPto);
        userDto.setAccountState(AccountState.INACTIVE);
        Pair<UserDto, String> registerResult = userFacade.registerUser(userDto, userRegistrationPto.getPassword());
        String confirmLink = "www.photogallery.com/confirm?email="
                + registerResult.getFirst().getEmail() + "&token=" + registerResult.getSecond(); // to be modified as appropriate
        boolean mail_ok = Mailer.sendConfirmRegistrationMail(userDto.getEmail(), confirmLink);
        if (!mail_ok) {
            log.error("Sending confirmation mail sending failed.");
            userFacade.deleteUser(registerResult.getFirst());
            model.addAttribute("failureMessage", "Registration failed. Please try again later.");
            return "/signup";
        }
        AuthenticationProviderImpl.logInUser(userDto, userRegistrationPto.getPassword());
        // user can be logged in after successful registration, that's not a problem
        return "redirect:/profile";
    }

    @RequestMapping(value = "/confirm")
    public String confirmUserRegistration(@RequestParam String email, @RequestParam String token, Model model) {
        boolean success = userFacade.confirmUserRegistration(email, token);
        model.addAttribute("success", success ? "success" : "failure");
        return "redirect:/profile";
    }

    @RequestMapping("/about")
    public String about() {
        return "about";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String userProfile(Model model) {
        String loginId = AuthenticationProviderImpl.getLoggedInUserIdentifier();
        Optional<UserDto> userDtoOptional = userFacade.findByIdentifier(loginId);
        if(!userDtoOptional.isPresent()){
            log.error("User with identifier " + loginId + " is supposed to log in but was not found.");
            return "/error";
        }
        UserProxy proxy = PhotoGalleryFrontendMapper.userDtoToProxy(userDtoOptional.get());
        model.addAttribute("userProxy", proxy);
        return "/profile";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam(required = false, defaultValue = "false") Boolean logout, Model model) {
        model.addAttribute("logout", logout);
        return "/login";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        AuthenticationProviderImpl.logout(request);
        return "redirect:/login?logout=true";
    }

}
