package cz.muni.fi.accessiblewebphotogallery.application.service.iface;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.time.Instant;
import java.util.Optional;

public interface PhotoService extends PagedService<PhotoEntity> {

    @Override
    PageImpl<PhotoEntity> findAll(Pageable pageable);

    PageImpl<PhotoEntity> findByUploadTimeBetween(Instant begin, Instant end, Pageable pageable);

    PageImpl<PhotoEntity> findByUploader(UserEntity uploader, Pageable Pageable);

    PageImpl<PhotoEntity> findByDescriptionContainingIgnoreCase(String searchStr, Pageable pageable);

    PageImpl<PhotoEntity> findByTitleContainingIgnoreCase(String searchStr, Pageable pageable);

    Optional<PhotoEntity> findByBase64Identifier(String b64id);

    PageImpl<PhotoEntity> findAllByOrderByUploadTimeDesc(Pageable pageable);

    PhotoEntity registerPhoto(PhotoEntity entity, File photoFile, File metadataFile);

    PhotoEntity updatePhoto(PhotoEntity photo);

    void deletePhoto(PhotoEntity photo);

}
