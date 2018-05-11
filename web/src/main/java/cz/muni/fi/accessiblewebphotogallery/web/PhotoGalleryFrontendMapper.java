package cz.muni.fi.accessiblewebphotogallery.web;

import cz.muni.fi.accessiblewebphotogallery.iface.dto.AlbumDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.BuildingInfoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AccountState;
import cz.muni.fi.accessiblewebphotogallery.web.pto.*;

public class PhotoGalleryFrontendMapper {

    public static UserPto userDtoToPto(UserDto dto){
        UserPto rv = new UserPto();
        rv.setId(dto.getId());
        rv.setAccountState(dto.getAccountState());
        rv.setEmail(dto.getEmail());
        rv.setLoginName(dto.getLoginName());
        rv.setScreenName(dto.getScreenName());
        rv.setBio(dto.getBio());
        return rv;
    }

    public static UserDto userRegistrationPtoToDto(UserRegistrationPto pto){
        UserDto rv = new UserDto();
        rv.setLoginName(pto.getLoginName());
        rv.setScreenName(pto.getScreenName());
        rv.setEmail(pto.getEmail());
        rv.setBio("");
        rv.setAccountState(AccountState.INACTIVE);
        return rv;
    }

    public static PhotoPto photoDtoToPto(PhotoDto dto){
        PhotoPto rv = new PhotoPto();
        rv.setId(dto.getId());
        rv.setUploader(userDtoToPto(dto.getUploader()));
        rv.setUploadTime(dto.getUploadTime());
        rv.setTitle(dto.getTitle());
        rv.setDescription(dto.getDescription());
        rv.setBase64Id(dto.getBase64Id());
        rv.setCameraLatitude(dto.getCameraLatitude());
        rv.setCameraLongitude(dto.getCameraLongitude());
        rv.setCameraAzimuth(dto.getCameraAzimuth());
        rv.setPositionAccuracy(dto.getPositionAccuracy());
        rv.setCameraFOV(dto.getCameraFOV());
        rv.setDatetimeTaken(dto.getDatetimeTaken());
        rv.setCameraModel(dto.getCameraModel());
        rv.setImageWidth(dto.getImageWidth());
        rv.setImageHeight(dto.getImageHeight());
        rv.setIso(dto.getIso());
        rv.setFlash(dto.getFlash());
        rv.setExposureTime(dto.getExposureTime());
        return rv;
    }

    public static PhotoDto photoPtoToDto(PhotoPto pto, UserDto userDto){
        PhotoDto rv = new PhotoDto();
        rv.setId(pto.getId());
        rv.setUploader(userDto);
        rv.setUploadTime(pto.getUploadTime());
        rv.setTitle(pto.getTitle());
        rv.setDescription(pto.getDescription());
        rv.setBase64Id(pto.getBase64Id());
        rv.setCameraLatitude(pto.getCameraLatitude());
        rv.setCameraLongitude(pto.getCameraLongitude());
        rv.setCameraAzimuth(pto.getCameraAzimuth());
        rv.setPositionAccuracy(pto.getPositionAccuracy());
        rv.setCameraFOV(pto.getCameraFOV());
        rv.setDatetimeTaken(pto.getDatetimeTaken());
        rv.setCameraModel(pto.getCameraModel());
        rv.setImageWidth(pto.getImageWidth());
        rv.setImageHeight(pto.getImageHeight());
        rv.setIso(pto.getIso());
        rv.setFlash(pto.getFlash());
        rv.setExposureTime(pto.getExposureTime());
        return rv;
    }

    public static BuildingInfoPto buildingDtoToPto(BuildingInfoDto dto){
        BuildingInfoPto rv = new BuildingInfoPto();
        rv.setId(dto.getId());
        rv.setPhoto(photoDtoToPto(dto.getPhoto()));
        rv.setOsmId(dto.getOsmId());
        rv.setDistance(dto.getDistance());
        rv.setBuildingName(dto.getBuildingName());
        rv.setLatitude(dto.getLatitude());
        rv.setLongitude(dto.getLongitude());
        rv.setPhotoMinX(dto.getPhotoMinX());
        rv.setPhotoMaxX(dto.getPhotoMaxX());
        rv.setFocusText(dto.getFocusText());
        return rv;
    }

    public static BuildingInfoDto buildingPtoToDto(BuildingInfoPto pto, PhotoDto photoDto){
        BuildingInfoDto rv = new BuildingInfoDto();
        rv.setId(pto.getId());
        rv.setPhoto(photoDto);
        rv.setOsmId(pto.getOsmId());
        rv.setDistance(pto.getDistance());
        rv.setBuildingName(pto.getBuildingName());
        rv.setLatitude(pto.getLatitude());
        rv.setLongitude(pto.getLongitude());
        rv.setPhotoMinX(pto.getPhotoMinX());
        rv.setPhotoMaxX(pto.getPhotoMaxX());
        rv.setFocusText(pto.getFocusText());
        return rv;
    }


    public static AlbumPto albumDtoToPto(AlbumDto dto){
        AlbumPto rv = new AlbumPto();
        rv.setId(dto.getId());
        rv.setAlbumOwner(userDtoToPto(dto.getAlbumOwner()));
        rv.setAlbumName(dto.getAlbumName());
        rv.setBase64Id(dto.getBase64Id());
        return rv;
    }

}
