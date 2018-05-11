package cz.muni.fi.accessiblewebphotogallery.web.controller;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cz.muni.fi.accessiblewebphotogallery.iface.ApplicationConfig;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.BuildingInfoFacade;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.PhotoFacade;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.UserFacade;
import cz.muni.fi.accessiblewebphotogallery.web.AuthenticationProviderImpl;
import cz.muni.fi.accessiblewebphotogallery.web.pto.PhotoPto;
import cz.muni.fi.accessiblewebphotogallery.web.validation.PhotoPtoValidator;
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
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Inject
    private Gson jsonConverter;

    private final Logger log = LogManager.getLogger(this);

    @InitBinder
    protected void initBinder(WebDataBinder binder){
        if(binder.getTarget() instanceof PhotoPto){
            binder.addValidators(new PhotoPtoValidator());
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
            String photoExt = photoOrigFileName.substring(photoOrigFileName.lastIndexOf('.'));
            String photoFNFinal = applicationConfig.getPhotoDirectory() + File.separator + uploader.get().getLoginName() + "_" + photoUploadTime.getEpochSecond() + photoExt;
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
            photoPto.setUploadTime(photoUploadTime);
            if(origMDFileName == null){
                return "redirect:/detail/" + createPhotoAndBuildingDBEntries(photoPto, new File(photoFNFinal), null);
            }
            String mdFNFinal = applicationConfig.getPhotoDirectory() + File.separator + uploader.get().getLoginName() + "_" + photoUploadTime.getEpochSecond() + ".json";
            try {
                photoFilePart.write(mdFNFinal);
            } catch (IOException e) {
                log.catching(e);
                log.error("IOException writing metadata file to location: " + mdFNFinal + "." + e.getMessage());
                return "/upload/form";
            } // alternatively, could just save w/o metadata...
            return "redirect:/detail/" + createPhotoAndBuildingDBEntries(photoPto, new File(photoFNFinal),new File(mdFNFinal));
        }
        return "/upload/form";
    }

    private String createPhotoAndBuildingDBEntries(PhotoPto photoPto, File photoFile, File metadataFile){
        boolean moveOk;
        Optional<UserDto> uploader = userFacade.findByIdentifier(AuthenticationProviderImpl.getLoggedUserLoginId());
        if(!uploader.isPresent()){
            throw new IllegalStateException("Logged in user not found in DB.");
        }
        PhotoDto photoDto = new PhotoDto();
        photoDto.setTitle(photoPto.getTitle());
        photoDto.setDescription(photoPto.getDescription());
        photoDto.setUploader(uploader.get());
        PhotoDto registeredPhoto = photoFacade.registerPhoto(photoDto,photoFile,metadataFile);
        String photoFileName = photoFile.getAbsolutePath().substring(photoFile.getAbsolutePath().lastIndexOf(File.separatorChar)+1);
        String photoExt = photoFileName.substring(photoFileName.lastIndexOf('.'));
        if(metadataFile != null){
            byte[] mdJsonRaw;
            try {
                mdJsonRaw = Files.readAllBytes(metadataFile.toPath());
            } catch (IOException e) {
                log.catching(e);
                log.error("Failed to process metadata file due to IOException: \n " + e.getMessage());
                photoFacade.deletePhoto(registeredPhoto);
                return null;
            }
            String mdJsonString = new String(mdJsonRaw);
            JsonArray mdJsonArray = jsonConverter.fromJson(mdJsonString,JsonArray.class);
            List<JsonObject> mdJsonList = new ArrayList<>();
            JsonObject cameraPositionJson = null;
            for(int i=0;i<mdJsonArray.size();i++){
                JsonObject tmp = mdJsonArray.get(i).getAsJsonObject();
                if(tmp.has("position")) mdJsonList.add(tmp);
                if(tmp.has("cameraposition")) cameraPositionJson = tmp;
            }
            Map<JsonObject,JsonObject> jsonMap = new HashMap<>();
            for(JsonObject b : mdJsonList){
                JsonObject pos = b.get("position").getAsJsonObject();
                double lat = pos.get("latitude").getAsDouble();
                double lon = pos.get("longitude").getAsDouble();
                RestTemplate nominatimRq = new RestTemplate();
                String restResult = nominatimRq.getForObject("https://nominatim.openstreetmap.org/reverse?format=json&lat={lat}&lon={lon}",String.class,lat,lon);
                JsonObject buildingJson = jsonConverter.fromJson(restResult,JsonObject.class);
                jsonMap.put(b,buildingJson);
                try {
                    TimeUnit.SECONDS.wait(1); // Nominatim wants an absolute maximum of 1 request per second;
                    // this is not necessary with own Nominatim installations, it's only here for demo purposes
                } catch (InterruptedException e) {
                    log.catching(e);
                    photoFacade.deletePhoto(registeredPhoto);
                    return null;
                }
            }
            if(cameraPositionJson == null || jsonMap.isEmpty()){
                log.error("Failed to pull data from supplied supposed metadata file: " + metadataFile.getAbsolutePath());
                photoFacade.deletePhoto(registeredPhoto);
                return null;
            }
            buildingFacade.registerBuildings(jsonMap,cameraPositionJson,registeredPhoto);
            File mdDest = new File(applicationConfig.getPhotoDirectory()+File.separator+registeredPhoto.getBase64Id()+".json");
            moveOk = metadataFile.renameTo(mdDest);
            if(!moveOk){
                log.error("Failed to move metadata file: " + metadataFile.getAbsolutePath() + " to destination: " + mdDest.getAbsolutePath());
                photoFacade.deletePhoto(registeredPhoto);
                return null;
            }
            //could work around pathing issues by assigning photo file and metadata file paths to DB entries(then again, Base-64 IDs already do part of that)
        }
        File photoDest = new File(applicationConfig.getPhotoDirectory()+File.separator+registeredPhoto.getBase64Id()+photoExt);
        moveOk = photoFile.renameTo(photoDest);
        if(!moveOk){
            log.error("Failed to move photo file: " + photoFile.getAbsolutePath() + " to destination: " + photoDest.getAbsolutePath());
            photoFacade.deletePhoto(registeredPhoto);
            return null;
        }
        return registeredPhoto.getBase64Id();
    }

}
