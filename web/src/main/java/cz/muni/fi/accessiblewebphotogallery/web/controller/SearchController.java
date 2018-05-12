package cz.muni.fi.accessiblewebphotogallery.web.controller;


import cz.muni.fi.accessiblewebphotogallery.application.ApplicationConfig;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.BuildingInfoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.BuildingInfoFacade;
import cz.muni.fi.accessiblewebphotogallery.web.PhotoGalleryFrontendMapper;
import cz.muni.fi.accessiblewebphotogallery.web.pto.PhotoPto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/search")
public class SearchController {

    @Inject
    BuildingInfoFacade buildingFacade;
    @Inject
    ApplicationConfig applicationConfig;

    @RequestMapping("/building")
    public String findPhotosByBuilding(Model model, @RequestParam(name = "osmId") Long osmId){
        List<BuildingInfoDto> buildingDtoList = buildingFacade.findByOsmId(osmId);
        List<PhotoDto> photoDtoList  = new ArrayList<>();
        for (BuildingInfoDto x : buildingDtoList){
            photoDtoList.add(x.getPhoto());
        }
        List<PhotoPto> photoPtoList = photoDtoList.stream().map(PhotoGalleryFrontendMapper::photoDtoToPto).collect(Collectors.toList());
        File photoDir = new File(applicationConfig.getPhotoDirectory());
        List<String> thumbnailPathList = new ArrayList<>();
        for (PhotoDto x: photoDtoList) {
            thumbnailPathList.add(photoDir.getAbsolutePath() +  File.separator + x.getBase64Id() + "_thumb.jpg");
        }
        model.addAttribute("photos",photoPtoList);
        model.addAttribute("thumbnails",thumbnailPathList);
        return "building";
    }

    @RequestMapping("/building")
    public String findPhotosByBuildingName(Model model, @RequestParam(name = "buildingName") String buildingName){
        List<BuildingInfoDto> buildingDtoList = buildingFacade.findByBuildingNameApx(buildingName);
        List<PhotoDto> photoDtoList  = new ArrayList<>();
        for (BuildingInfoDto x : buildingDtoList){
            photoDtoList.add(x.getPhoto());
        }
        List<PhotoPto> photoPtoList = photoDtoList.stream().map(PhotoGalleryFrontendMapper::photoDtoToPto).collect(Collectors.toList());
        File photoDir = new File(applicationConfig.getPhotoDirectory());
        List<String> thumbnailPathList = new ArrayList<>();
        for (PhotoDto x: photoDtoList) {
            thumbnailPathList.add(photoDir.getAbsolutePath() +  File.separator + x.getBase64Id() + "_thumb.jpg");
        }
        model.addAttribute("photos",photoPtoList);
        model.addAttribute("thumbnails",thumbnailPathList);
        return "building";
    }


}
