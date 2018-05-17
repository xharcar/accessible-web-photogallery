package cz.muni.fi.accessiblewebphotogallery.application;

import cz.muni.fi.accessiblewebphotogallery.facade.dto.AlbumDto;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.BuildingInfoDto;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AlbumEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.BuildingInfo;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;

// Dozer mappers don;t know what an Instant is, and upon attempting to use a compatibility library, parsing errors are being thrown
// So I'm rolling my own single-purpose mapper here
public class PhotoGalleryBackendMapper {

    public static PhotoDto photoEntityToDto(PhotoEntity entity) {
        PhotoDto rv = new PhotoDto();
        rv.setId(entity.getId());
        rv.setUploader(userEntityToDto(entity.getUploader()));
        rv.setUploadTime(entity.getUploadTime());
        rv.setTitle(entity.getTitle());
        rv.setDescription(entity.getDescription());
        rv.setCameraLatitude(entity.getCameraLatitude());
        rv.setCameraLongitude(entity.getCameraLongitude());
        rv.setCameraAzimuth(entity.getCameraAzimuth());
        rv.setPositionAccuracy(entity.getPositionAccuracy());
        rv.setCameraFOV(entity.getCameraFOV());
        rv.setDatetimeTaken(entity.getDatetimeTaken());
        rv.setCameraModel(entity.getCameraModel());
        rv.setImageWidth(entity.getImageWidth());
        rv.setImageHeight(entity.getImageHeight());
        rv.setIso(entity.getIso());
        rv.setFlash(entity.getFlash());
        rv.setExposureTime(entity.getExposureTime());
        return rv;
    }

    public static PhotoEntity photoDtoToEntity(PhotoDto dto) {
        PhotoEntity rv = new PhotoEntity();
        rv.setId(dto.getId());
        rv.setUploader(userDtoToEntity(dto.getUploader()));
        rv.setUploadTime(dto.getUploadTime());
        rv.setTitle(dto.getTitle());
        rv.setDescription(dto.getDescription());
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

    public static UserDto userEntityToDto(UserEntity entity) {
        UserDto rv = new UserDto();
        rv.setId(entity.getId());
        rv.setLoginName(entity.getLoginName());
        rv.setScreenName(entity.getScreenName());
        rv.setEmail(entity.getEmail());
        rv.setPasswordHash(entity.getPasswordHash());
        rv.setPasswordSalt(entity.getPasswordSalt());
        rv.setAccountState(entity.getAccountState());
        rv.setBio(entity.getBio());
        return rv;
    }

    public static UserEntity userDtoToEntity(UserDto dto) {
        UserEntity rv = new UserEntity();
        rv.setId(dto.getId());
        rv.setLoginName(dto.getLoginName());
        rv.setScreenName(dto.getScreenName());
        rv.setEmail(dto.getEmail());
        rv.setPasswordHash(dto.getPasswordHash());
        rv.setPasswordSalt(dto.getPasswordSalt());
        rv.setAccountState(dto.getAccountState());
        rv.setBio(dto.getBio());
        return rv;
    }

    public static BuildingInfoDto buildingInfoToDto(BuildingInfo entity) {
        BuildingInfoDto rv = new BuildingInfoDto();
        rv.setId(entity.getId());
        rv.setPhoto(photoEntityToDto(entity.getPhoto()));
        rv.setOsmId(entity.getOsmId());
        rv.setDistance(entity.getDistance());
        rv.setBuildingName(entity.getBuildingName());
        rv.setLatitude(entity.getLatitude());
        rv.setLongitude(entity.getLongitude());
        rv.setPhotoMinX(entity.getPhotoMinX());
        rv.setPhotoMaxX(entity.getPhotoMaxX());
        rv.setFocusText(entity.getFocusText());
        return rv;
    }

    public static BuildingInfo buildingInfoDtoToEntity(BuildingInfoDto dto) {
        BuildingInfo rv = new BuildingInfo();
        rv.setId(dto.getId());
        rv.setPhoto(photoDtoToEntity(dto.getPhoto()));
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

    public static AlbumDto albumEntityToDto(AlbumEntity entity) {
        AlbumDto rv = new AlbumDto();
        rv.setId(entity.getId());
        rv.setOwner(userEntityToDto(entity.getOwner()));
        rv.setAlbumName(entity.getName());
        rv.setId(entity.getId());
        return rv;
    }

    public static AlbumEntity albumDtoToEntity(AlbumDto dto) {
        AlbumEntity rv = new AlbumEntity();
        rv.setId(dto.getId());
        rv.setOwner(userDtoToEntity(dto.getOwner()));
        rv.setName(dto.getAlbumName());
        rv.setId(dto.getId());
        return rv;
    }

}
