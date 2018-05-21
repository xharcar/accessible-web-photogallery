package cz.muni.fi.accessiblewebphotogallery;

import cz.muni.fi.accessiblewebphotogallery.facade.dto.AlbumDto;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.proxy.AlbumProxy;
import cz.muni.fi.accessiblewebphotogallery.proxy.PhotoProxy;
import cz.muni.fi.accessiblewebphotogallery.proxy.UserProxy;
import cz.muni.fi.accessiblewebphotogallery.proxy.UserRegistrationProxy;

public class PhotoGalleryFrontendMapper {

    public static UserProxy userDtoToProxy(UserDto dto){
        UserProxy rv = new UserProxy();
        rv.setAccountState(dto.getAccountState());
        rv.setBio(dto.getBio());
        rv.setEmail(dto.getEmail());
        rv.setLoginName(dto.getLoginName());
        rv.setScreenName(dto.getScreenName());
        return rv;
    }

    public static UserDto userProxyToDto(UserProxy proxy){
        UserDto rv = new UserDto();
        rv.setAccountState(proxy.getAccountState());
        rv.setBio(proxy.getBio());
        rv.setEmail(proxy.getEmail());
        rv.setLoginName(proxy.getLoginName());
        rv.setScreenName(proxy.getScreenName());
        return rv;
    }

    public static UserDto userRegistrationProxyToDto(UserRegistrationProxy regProxy){
        UserDto rv = new UserDto();
        rv.setLoginName(regProxy.getLoginName());
        rv.setEmail(regProxy.getEmail());
        return rv;
    }


    public static PhotoProxy photoDtoToProxy(PhotoDto dto){
        PhotoProxy rv = new PhotoProxy();
        rv.setUploader(userDtoToProxy(dto.getUploader()));
        rv.setBuildingList(dto.getBuildingList());
        rv.setCameraAzimuth(dto.getCameraAzimuth());
        rv.setCameraFOV(dto.getCameraFOV());
        rv.setCameraLatitude(dto.getCameraLatitude());
        rv.setCameraLongitude(dto.getCameraLongitude());
        rv.setDatetimeTaken(dto.getDatetimeTaken());
        rv.setCameraModel(dto.getCameraModel());
        rv.setDescription(dto.getDescription());
        rv.setExposureTime(dto.getExposureTime());
        rv.setFlash(dto.getFlash());
        rv.setId(dto.getId());
        rv.setImageHeight(dto.getImageHeight());
        rv.setImageWidth(dto.getImageWidth());
        rv.setIso(dto.getIso());
        rv.setPositionAccuracy(dto.getPositionAccuracy());
        rv.setTitle(dto.getTitle());
        rv.setUploadTime(dto.getUploadTime());
        return rv;
    }

    public static PhotoDto photoProxyToDto(PhotoProxy proxy){
        PhotoDto rv = new PhotoDto();
        rv.setUploader(userProxyToDto(proxy.getUploader()));
        rv.setBuildingList(proxy.getBuildingList());
        rv.setCameraAzimuth(proxy.getCameraAzimuth());
        rv.setCameraFOV(proxy.getCameraFOV());
        rv.setCameraLatitude(proxy.getCameraLatitude());
        rv.setCameraLongitude(proxy.getCameraLongitude());
        rv.setDatetimeTaken(proxy.getDatetimeTaken());
        rv.setCameraModel(proxy.getCameraModel());
        rv.setDescription(proxy.getDescription());
        rv.setExposureTime(proxy.getExposureTime());
        rv.setFlash(proxy.getFlash());
        rv.setId(proxy.getId());
        rv.setImageHeight(proxy.getImageHeight());
        rv.setImageWidth(proxy.getImageWidth());
        rv.setIso(proxy.getIso());
        rv.setPositionAccuracy(proxy.getPositionAccuracy());
        rv.setTitle(proxy.getTitle());
        rv.setUploadTime(proxy.getUploadTime());
        return rv;
    }

    public static AlbumProxy albumDtoToProxy(AlbumDto dto){
        AlbumProxy rv = new AlbumProxy();
        rv.setOwner(userDtoToProxy(dto.getOwner()));
        rv.setId(dto.getId());
        rv.setName(dto.getAlbumName());
        return rv;
    }

    public static AlbumDto albumProxyToDto(AlbumProxy proxy){
        AlbumDto rv = new AlbumDto();
        rv.setOwner(userProxyToDto(proxy.getOwner()));
        rv.setId(proxy.getId());
        rv.setAlbumName(proxy.getName());
        return rv;
    }


}
