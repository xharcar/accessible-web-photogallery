package cz.muni.fi.accessiblewebphotogallery.controller;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cz.muni.fi.accessiblewebphotogallery.AuthenticationProviderImpl;
import cz.muni.fi.accessiblewebphotogallery.application.ApplicationConfig;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.facade.facade.PhotoFacade;
import cz.muni.fi.accessiblewebphotogallery.facade.facade.UserFacade;
import cz.muni.fi.accessiblewebphotogallery.proxy.PhotoProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

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
    private PhotoFacade photoFacade;
    private UserFacade userFacade;
    private ApplicationConfig applicationConfig;
    private Gson jsonConverter;
    private static final Logger log = LogManager.getLogger(UploadController.class);

    @Autowired
    public UploadController(PhotoFacade pf, UserFacade uf, ApplicationConfig ac){
        photoFacade = pf;
        userFacade = uf;
        applicationConfig = ac;
        jsonConverter = new Gson();
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String uploadFormGet(Model model) {
        PhotoProxy photoProxy = new PhotoProxy();
        model.addAttribute("photoProxy", photoProxy);
        return "/upload/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String uploadFormPost(@ModelAttribute @Valid PhotoProxy photoProxy, BindingResult bindingResult,
                                 HttpServletRequest request) {
        if (!bindingResult.hasErrors()) {
            Optional<UserDto> uploader = userFacade.findByIdentifier(AuthenticationProviderImpl.getLoggedInUserIdentifier());
            if (!uploader.isPresent()) {
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
            if (photoOrigFileName == null) {
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
                log.error("Error writing uploaded photo file to location: " + photoFNFinal + "." + e.getMessage());
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
            photoProxy.setUploadTime(photoUploadTime);
            if (origMDFileName == null) {
                return "redirect:" + createPhotoAndBuildingDBEntries(photoProxy, new File(photoFNFinal), null);
                // /detail?photo=
            }
            String mdFNFinal = applicationConfig.getPhotoDirectory() + File.separator + uploader.get().getLoginName() + "_" + photoUploadTime.getEpochSecond() + ".json";
            try {
                metadataFilePart.write(mdFNFinal);
            } catch (IOException e) {
                log.catching(e);
                log.error("IOException writing metadata file to location: " + mdFNFinal + "." + e.getMessage());
                return "/upload/form";
            } // alternatively, could just save w/o metadata...
            return "redirect:" + createPhotoAndBuildingDBEntries(photoProxy, new File(photoFNFinal), new File(mdFNFinal));
            // /detail?photo=
        }
        return "/upload/form";
    }

    private String createPhotoAndBuildingDBEntries(PhotoProxy photoProxy, File photoFile, File metadataFile) {
        Optional<UserDto> uploader = userFacade.findByIdentifier(AuthenticationProviderImpl.getLoggedInUserIdentifier());
        if (!uploader.isPresent()) {
            throw new IllegalStateException("Logged in user not found in DB.");
        }
        PhotoDto photoDto = new PhotoDto();
        photoDto.setTitle(photoProxy.getTitle());
        photoDto.setDescription(photoProxy.getDescription());
        photoDto.setUploader(uploader.get());
        String photoFileName = photoFile.getAbsolutePath().substring(photoFile.getAbsolutePath().lastIndexOf(File.separatorChar) + 1);
        String photoExt = photoFileName.substring(photoFileName.lastIndexOf('.'));
        Map<JsonObject,JsonObject> buildingMap = null;
        JsonObject cameraJson = null;
        if (metadataFile != null) {
            byte[] mdJsonRaw;
            try {
                mdJsonRaw = Files.readAllBytes(metadataFile.toPath());
            } catch (IOException e) {
                log.catching(e);
                log.error("Failed to process metadata file due to IOException: \n " + e.getMessage());
                return "/upload/form";
            }
            String mdJsonString = new String(mdJsonRaw);
            JsonArray mdJsonArray = jsonConverter.fromJson(mdJsonString, JsonArray.class);
            List<JsonObject> mdJsonList = new ArrayList<>();
            cameraJson = null;
            for (int i = 0; i < mdJsonArray.size(); i++) {
                JsonObject tmp = mdJsonArray.get(i).getAsJsonObject();
                if (tmp.has("position")) mdJsonList.add(tmp);
                if (tmp.has("cameraposition")) cameraJson = tmp;
            }
            buildingMap = new HashMap<>();
            for (JsonObject b : mdJsonList) {
                JsonObject pos = b.get("position").getAsJsonObject();
                double lat = pos.get("latitude").getAsDouble();
                double lon = pos.get("longitude").getAsDouble();
                RestTemplate nominatimRq = new RestTemplate();
                String restResult = nominatimRq.getForObject("https://nominatim.openstreetmap.org/reverse?format=json&lat={lat}&lon={lon}", String.class, lat, lon);
                log.info("restResult = " + restResult);
                JsonObject buildingJson = jsonConverter.fromJson(restResult, JsonObject.class);
                buildingMap.put(b, buildingJson);
                try {
                    TimeUnit.SECONDS.wait(1); // Nominatim wants an absolute maximum of 1 request per second;
                    // this is not necessary with own Nominatim installations, it's only here for demo purposes
                } catch (InterruptedException e) {
                    log.catching(e);
                    return "/upload/form";
                }
            }
            if (cameraJson == null || buildingMap.isEmpty()) {
                log.error("Failed to pull data from supplied supposed metadata file: " + metadataFile.getAbsolutePath());
                return "/upload/form";
            }
        }
        PhotoDto registeredPhoto = photoFacade.registerPhoto(photoDto,photoFile,metadataFile,buildingMap,cameraJson);
        return registeredPhoto.getId();
    }

}
