package cz.muni.fi.accessiblewebphotogallery.application.facade;

import cz.muni.fi.accessiblewebphotogallery.application.PhotoGalleryBackendMapper;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.PhotoService;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.PhotoFacade;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PhotoFacadeImpl implements PhotoFacade {

    private PhotoService photoService;

    @Inject
    public PhotoFacadeImpl(PhotoService photoService) {
        this.photoService = photoService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageImpl<PhotoDto> findByUploadTimeBetween(Instant begin, Instant end, Pageable pageable) {
        PageImpl<PhotoEntity> entityPage = photoService.findByUploadTimeBetween(begin, end, pageable);
        return new PageImpl<>(entityPage.getContent().stream().map(this::photoToDto).collect(Collectors.toList()), pageable, entityPage.getTotalElements());
    }

    @Override
    public PageImpl<PhotoDto> findByUploader(UserDto uploader, Pageable pageable) {
        PageImpl<PhotoEntity> entityPage = photoService.findByUploader(userDtoToEntity(uploader), pageable);
        return new PageImpl<>(entityPage.getContent().stream().map(this::photoToDto).collect(Collectors.toList()), pageable, entityPage.getTotalElements());
    }

    @Override
    public PageImpl<PhotoDto> findByDescPartIgnoreCase(String partialDescription, Pageable pageable) {
        PageImpl<PhotoEntity> entityPage = photoService.findByDescriptionApx(partialDescription, pageable);
        return new PageImpl<>(entityPage.getContent().stream().map(this::photoToDto).collect(Collectors.toList()), pageable, entityPage.getTotalElements());
    }

    @Override
    public PageImpl<PhotoDto> findByTitlePartIgnoreCase(String partialTitle, Pageable pageable) {
        PageImpl<PhotoEntity> entityPage = photoService.findByTitleApx(partialTitle, pageable);
        return new PageImpl<>(entityPage.getContent().stream().map(this::photoToDto).collect(Collectors.toList()), pageable, entityPage.getTotalElements());
    }

    @Override
    public Optional<PhotoDto> findById(Long id) {
        return photoService.findById(id).map(PhotoGalleryBackendMapper::photoEntityToDto);
    }

    @Override
    public Optional<PhotoDto> findByBase64Id(String base64Id) {
        return photoService.findByBase64Id(base64Id).map(this::photoToDto);
    }

    @Override
    public PageImpl<PhotoDto> findNewestFirst(Pageable pageable) {
        PageImpl<PhotoEntity> entityPage = photoService.findNewestFirst(pageable);
        return new PageImpl<>(entityPage.getContent().stream().map(this::photoToDto).collect(Collectors.toList()), pageable, entityPage.getTotalElements());
    }

    @Override
    // for looking up albums
    public PageImpl<PhotoDto> findMultipleByBase64(List<String> b64ids, Pageable pageable) {
        List<PhotoDto> photoDtoList = new ArrayList<>();
        for(String id : b64ids){
            Optional<PhotoDto> dtoOpt = findByBase64Id(id);
            if(dtoOpt.isPresent()){
                photoDtoList.add(dtoOpt.get());
            }
        }
        return new PageImpl<>(photoDtoList,pageable,photoDtoList.size());
    }

    @Override
    public PhotoDto registerPhoto(PhotoDto photo, File photoFile, File metadataFile) {
        return photoToDto(photoService.registerPhoto(photoDtoToEntity(photo),photoFile,metadataFile));
    }

    @Override
    public PhotoDto updatePhoto(PhotoDto photoDto) {
        return photoToDto(photoService.updatePhoto(photoDtoToEntity(photoDto)));
    }

    @Override
    public boolean clearPhoto(PhotoDto photo) {
        return photoService.clearPhoto(photoDtoToEntity(photo));
    }

    public void deletePhoto(PhotoDto photo){photoService.deletePhoto(photoDtoToEntity(photo));}

    private UserEntity userDtoToEntity(UserDto userDto) {
        return PhotoGalleryBackendMapper.userDtoToEntity(userDto);
    }

    private PhotoDto photoToDto(PhotoEntity entity) {
        return PhotoGalleryBackendMapper.photoEntityToDto(entity);
    }

    private PhotoEntity photoDtoToEntity(PhotoDto dto) {
        return PhotoGalleryBackendMapper.photoDtoToEntity(dto);
    }
}
