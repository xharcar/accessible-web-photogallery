package cz.muni.fi.accessiblewebphotogallery.facade.facade;

import com.google.gson.JsonObject;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.AlbumDto;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.UserDto;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PhotoFacade {

    PageImpl<PhotoDto> findPhotosByUploadTimeBetween(Instant begin, Instant end, Pageable pageable);

    PageImpl<PhotoDto> findPhotosByUploader(UserDto uploader, Pageable pageable);

    PageImpl<PhotoDto> findPhotosByDescPartIgnoreCase(String partialDescription, Pageable pageable);

    PageImpl<PhotoDto> findPhotosByTitlePartIgnoreCase(String partialTitle, Pageable pageable);

    Optional<PhotoDto> findPhotoById(String id);

    PageImpl<PhotoDto> findNewestPhotosFirst(Pageable pageable);

    List<PhotoDto> findPhotosInAlbum(AlbumDto albumDto);

    List<PhotoDto> findPhotosByBuildingNameApx(String buildingNameApx);

    PhotoDto findNextPhotoInAlbum(AlbumDto albumDto, PhotoDto photoDto);

    PhotoDto findPreviousPhotoInAlbum(AlbumDto albumDto, PhotoDto photoDto);

    Optional<PhotoDto> findNextUploadedPhoto(PhotoDto photoDto);

    Optional<PhotoDto> findPreviousUploadedPhoto(PhotoDto photoDto);

    PhotoDto registerPhoto(PhotoDto photo, File photoFile, File metadataFile, Map<JsonObject,JsonObject> buildingMap, JsonObject cameraData);

    PhotoDto updatePhoto(PhotoDto photoDto);

    boolean clearPhoto(PhotoDto photo);

    public void deletePhoto(PhotoDto photo);

}
