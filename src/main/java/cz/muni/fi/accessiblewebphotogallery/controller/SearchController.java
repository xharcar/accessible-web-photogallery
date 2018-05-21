//package cz.muni.fi.accessiblewebphotogallery.web.controller;
//
//
//import cz.muni.fi.accessiblewebphotogallery.PhotoGalleryFrontendMapper;
//import cz.muni.fi.accessiblewebphotogallery.application.ApplicationConfig;
//import cz.muni.fi.accessiblewebphotogallery.facade.dto.BuildingInfoDto;
//import cz.muni.fi.accessiblewebphotogallery.facade.dto.PhotoDto;
//import cz.muni.fi.accessiblewebphotogallery.facade.facade.PhotoFacade;
//import cz.muni.fi.accessiblewebphotogallery.facade.facade.UserFacade;
//import cz.muni.fi.accessiblewebphotogallery.proxy.PhotoProxy;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Controller
//@RequestMapping("/search")
//public class SearchController {
//
//    private PhotoFacade photoFacade;
//    private UserFacade userFacade;
//    private ApplicationConfig applicationConfig;
//
//    @Autowired
//    public SearchController(PhotoFacade pf, UserFacade uf, ApplicationConfig ac){
//        photoFacade = pf;
//        userFacade = uf;
//        applicationConfig = ac;
//    }
//
//    @RequestMapping("")
//    public String searchPage(Model model){
//        model.addAttribute("title","Search");
//
//        return "";
//    }
//
//
//
//    @RequestMapping("/building_id")
//    public String findPhotosByBuilding(Model model, @RequestParam(name = "building") String buildingId) {
//        List<PhotoDto> photoDtoList = photoFacade.findPhotosByBuilding(buildingId);
//        List<PhotoProxy> photoProxyList = photoDtoList.stream().map(PhotoGalleryFrontendMapper::photoDtoToProxy).collect(Collectors.toList());
//        File photoDir = new File(applicationConfig.getPhotoDirectory());
//        List<String> thumbnailPathList = new ArrayList<>();
//        for (PhotoDto x : photoDtoList) {
//            thumbnailPathList.add(photoDir.getAbsolutePath() + File.separator + x.getId() + "_thumb.jpg");
//        }
//        model.addAttribute("photos", photoProxyList);
//        model.addAttribute("thumbnails", thumbnailPathList);
//        return "/building";
//    }
//
//    @RequestMapping("/building_name")
//    public String findPhotosByBuildingName(Model model, @RequestParam(name = "name") String buildingName) {
//        List<PhotoProxy> photoProxyList = photoFacade.findPhotosByBuildingNameApx(buildingName).stream().map(PhotoGalleryFrontendMapper::photoDtoToProxy).collect(Collectors.toList());
//        File photoDir = new File(applicationConfig.getPhotoDirectory());
//        List<String> thumbnailPathList = new ArrayList<>();
//        for (PhotoProxy x : photoProxyList) {
//            thumbnailPathList.add(photoDir.getAbsolutePath() + File.separator + x.getId() + "_thumb.jpg");
//        }
//        model.addAttribute("photos", photoProxyList);
//        model.addAttribute("thumbnails", thumbnailPathList);
//        return "building";
//    }
//
//
//
//}
