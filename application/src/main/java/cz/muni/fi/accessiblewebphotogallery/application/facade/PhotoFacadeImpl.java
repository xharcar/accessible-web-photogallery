package cz.muni.fi.accessiblewebphotogallery.application.facade;

import com.google.gson.JsonObject;
import cz.muni.fi.accessiblewebphotogallery.application.ApplicationConfig;
import cz.muni.fi.accessiblewebphotogallery.application.PhotoGalleryBackendMapper;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.AlbumService;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.BuildingInfoService;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.PhotoService;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.AlbumDto;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.facade.facade.PhotoFacade;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.BuildingInfo;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PhotoFacadeImpl implements PhotoFacade {

    private PhotoService photoService;
    private BuildingInfoService buildingService;
    private AlbumService albumService;
    private ApplicationConfig applicationConfig;
    private static final Logger log = LogManager.getLogger(PhotoFacadeImpl.class);

    @Autowired
    public PhotoFacadeImpl(PhotoService photoService, BuildingInfoService buildingInfoService, AlbumService albumService, ApplicationConfig applicationConfig){
        this.photoService = photoService;
        this.buildingService = buildingInfoService;
        this.albumService = albumService;
        this.applicationConfig = applicationConfig;
    }

    @Override
    public PageImpl<PhotoDto> findPhotosByUploadTimeBetween(Instant begin, Instant end, Pageable pageable) {
        PageImpl<PhotoEntity> entityPage = photoService.findByUploadTimeBetween(begin, end, pageable);
        List<PhotoDto> dtoList = new ArrayList<>();
        for(PhotoEntity entity : entityPage.getContent()){
            dtoList.add(PhotoGalleryBackendMapper.photoEntityPlusBuildingsToDto(entity,buildingService.findByPhoto(entity)));
        }
        return new PageImpl<>(dtoList,pageable,entityPage.getTotalElements());
    }

    @Override
    public PageImpl<PhotoDto> findPhotosByUploader(UserDto uploader, Pageable pageable) {
        PageImpl<PhotoEntity> entityPage = photoService.findByUploader(PhotoGalleryBackendMapper.userDtoToEntity(uploader), pageable);
        List<PhotoDto> dtoList = new ArrayList<>();
        for(PhotoEntity entity : entityPage.getContent()){
            dtoList.add(PhotoGalleryBackendMapper.photoEntityPlusBuildingsToDto(entity,buildingService.findByPhoto(entity)));
        }
        return new PageImpl<>(dtoList,pageable,entityPage.getTotalElements());
    }

    @Override
    public PageImpl<PhotoDto> findPhotosByDescPartIgnoreCase(String partialDescription, Pageable pageable) {
        PageImpl<PhotoEntity> entityPage = photoService.findByDescriptionApx(partialDescription, pageable);
        List<PhotoDto> dtoList = new ArrayList<>();
        for(PhotoEntity entity : entityPage.getContent()){
            dtoList.add(PhotoGalleryBackendMapper.photoEntityPlusBuildingsToDto(entity,buildingService.findByPhoto(entity)));
        }
        return new PageImpl<>(dtoList,pageable,entityPage.getTotalElements());
    }

    @Override
    public PageImpl<PhotoDto> findPhotosByTitlePartIgnoreCase(String partialTitle, Pageable pageable) {
        PageImpl<PhotoEntity> entityPage = photoService.findByTitleApx(partialTitle, pageable);
        List<PhotoDto> dtoList = new ArrayList<>();
        for(PhotoEntity entity : entityPage.getContent()){
            dtoList.add(PhotoGalleryBackendMapper.photoEntityPlusBuildingsToDto(entity,buildingService.findByPhoto(entity)));
        }
        return new PageImpl<>(dtoList,pageable,entityPage.getTotalElements());
    }

    @Override
    public Optional<PhotoDto> findPhotoById(String id){
        Optional<PhotoEntity> photoEntityOpt = photoService.findById(id);
        if(!photoEntityOpt.isPresent()){
            return Optional.empty();
        }
        List<BuildingInfo> buildingInfoList = buildingService.findByPhoto(photoEntityOpt.get());
        return Optional.of(PhotoGalleryBackendMapper.photoEntityPlusBuildingsToDto(photoEntityOpt.get(),buildingInfoList));
    }


    @Override
    public PageImpl<PhotoDto> findNewestPhotosFirst(Pageable pageable) {
        PageImpl<PhotoEntity> entityPage = photoService.findNewestFirst(pageable);
        List<PhotoDto> dtoList = new ArrayList<>();
        for(PhotoEntity entity : entityPage.getContent()){
            dtoList.add(PhotoGalleryBackendMapper.photoEntityPlusBuildingsToDto(entity,buildingService.findByPhoto(entity)));
        }
        return new PageImpl<>(dtoList,pageable,entityPage.getTotalElements());
    }

    @Override
    public List<PhotoDto> findPhotosInAlbum(AlbumDto albumDto) {
        List<String> photoIdList = albumService.listPhotosInAlbum(PhotoGalleryBackendMapper.albumDtoToEntity(albumDto));
        List<PhotoEntity> photoEntityList = photoService.findMultipleById(photoIdList);
        List<PhotoDto> rv = new ArrayList<>();
        for(PhotoEntity ent:photoEntityList){
            rv.add(PhotoGalleryBackendMapper.photoEntityPlusBuildingsToDto(ent,buildingService.findByPhoto(ent)));
        }
        return rv;
    }

    @Override
    public List<PhotoDto> findPhotosByBuildingNameApx(String buildingNameApx) {
        List<BuildingInfo> buildingList = buildingService.findByBuildingNameApx(buildingNameApx);
        List<PhotoDto> rv = new ArrayList<>();
        for(BuildingInfo bi : buildingList){
            rv.add(PhotoGalleryBackendMapper.photoEntityPlusBuildingsToDto(bi.getPhoto(),buildingService.findByPhoto(bi.getPhoto())));
        }
        return rv;
    }

    @Override
    public PhotoDto findNextPhotoInAlbum(AlbumDto albumDto, PhotoDto photoDto){
        List<String> albumPhotoList = albumService.listPhotosInAlbum(PhotoGalleryBackendMapper.albumDtoToEntity(albumDto));
        int nextPhotoIdx = (albumPhotoList.indexOf(photoDto.getId()) + 1) % albumPhotoList.size();
        Optional<PhotoEntity> nextPhoto = photoService.findById(albumPhotoList.get(nextPhotoIdx));
        if(!nextPhoto.isPresent()){
            PhotoDto rv = new PhotoDto();
            rv.setId(albumPhotoList.get(nextPhotoIdx));
            return rv;
            // yes, an almost-null PhotoDto; that way we can easily tell a photo has been deleted and display a message telling the user that
        }
        return PhotoGalleryBackendMapper.photoEntityPlusBuildingsToDto(nextPhoto.get(),buildingService.findByPhoto(nextPhoto.get()));
    }

    @Override
    public PhotoDto findPreviousPhotoInAlbum(AlbumDto albumDto, PhotoDto photoDto) {
        List<String> albumPhotoList = albumService.listPhotosInAlbum(PhotoGalleryBackendMapper.albumDtoToEntity(albumDto));
        int prevPhotoIdx = (albumPhotoList.indexOf(photoDto.getId()) + albumPhotoList.size() - 1) % albumPhotoList.size();
        // because indexOf() can return 0, rendering the result negative, resulting in an IndexOutOfBoundsException
        Optional<PhotoEntity> prevPhoto = photoService.findById(albumPhotoList.get(prevPhotoIdx));
        if(!prevPhoto.isPresent()){
            PhotoDto rv = new PhotoDto();
            rv.setId(albumPhotoList.get(prevPhotoIdx));
            return rv;
            // see findNextPhotoInAlbum
        }
        return PhotoGalleryBackendMapper.photoEntityPlusBuildingsToDto(prevPhoto.get(),buildingService.findByPhoto(prevPhoto.get()));
    }

    @Override
    public Optional<PhotoDto> findNextUploadedPhoto(PhotoDto photoDto) {
        Optional<PhotoEntity> nextEntity = photoService.findNextUploaded(PhotoGalleryBackendMapper.photoDtoToEntityPlusBuildings(photoDto).getFirst());
        if(!nextEntity.isPresent()){
            return Optional.empty();
        }
        return Optional.of(PhotoGalleryBackendMapper.photoEntityPlusBuildingsToDto(nextEntity.get(), buildingService.findByPhoto(nextEntity.get())));
    }

    @Override
    public Optional<PhotoDto> findPreviousUploadedPhoto(PhotoDto photoDto) {
        Optional<PhotoEntity> prevEntity = photoService.findPreviouslyUploaded(PhotoGalleryBackendMapper.photoDtoToEntityPlusBuildings(photoDto).getFirst());
        if(!prevEntity.isPresent()){
            return Optional.empty();
        }
        return Optional.of(PhotoGalleryBackendMapper.photoEntityPlusBuildingsToDto(prevEntity.get(), buildingService.findByPhoto(prevEntity.get())));
    }

    @Override
    public PhotoDto registerPhoto(PhotoDto photoDto, File photoFile, File metadataFile, Map<JsonObject,JsonObject> buildingMap, JsonObject cameraData) {
        boolean moveOk;
        PhotoEntity entity = PhotoGalleryBackendMapper.photoDtoToEntityPlusBuildings(photoDto).getFirst(); // there's nothing in the second element now anyway
        entity = photoService.registerPhoto(entity,photoFile,metadataFile);
        String photoFileName = photoFile.getAbsolutePath().substring(photoFile.getAbsolutePath().lastIndexOf(File.separatorChar) + 1);
        String photoExt = photoFileName.substring(photoFileName.lastIndexOf('.'));
        File photoDest = new File(applicationConfig.getPhotoDirectory() + File.separator + entity.getId() + photoExt);
        moveOk = photoFile.renameTo(photoDest);
        if(!moveOk){
            log.error("Failed to move photo file: " + photoFile.getAbsolutePath() + " to destination: " + photoDest.getAbsolutePath());
            photoService.deletePhoto(entity);
            return null;
        }
        List<BuildingInfo> buildingList = new ArrayList<>();
        if(metadataFile != null) {
            buildingList = buildingService.registerBuildings(buildingMap, cameraData, entity);
            File mdDest = new File(applicationConfig.getPhotoDirectory() + File.separator + entity.getId() + ".json");
            moveOk = metadataFile.renameTo(mdDest);
            if (!moveOk) {
                log.error("Failed to move metadata file: " + metadataFile.getAbsolutePath() + " to destination: " + mdDest.getAbsolutePath());
                photoService.deletePhoto(entity);
                return null;
            }
        }
        return PhotoGalleryBackendMapper.photoEntityPlusBuildingsToDto(entity,buildingList);
    }

    @Override
    public PhotoDto updatePhoto(PhotoDto photoDto) {
        Pair<PhotoEntity,List<BuildingInfo>> photoValuePair = PhotoGalleryBackendMapper.photoDtoToEntityPlusBuildings(photoDto);
        return PhotoGalleryBackendMapper.photoEntityPlusBuildingsToDto(photoService.updatePhoto(photoValuePair.getFirst()),photoValuePair.getSecond());
    }

    @Override
    public boolean clearPhoto(PhotoDto photo) {
        return photoService.clearPhoto(PhotoGalleryBackendMapper.photoDtoToEntityPlusBuildings(photo).getFirst());
    }

    public void deletePhoto(PhotoDto photo) {
        photoService.deletePhoto(PhotoGalleryBackendMapper.photoDtoToEntityPlusBuildings(photo).getFirst());
    }

}
