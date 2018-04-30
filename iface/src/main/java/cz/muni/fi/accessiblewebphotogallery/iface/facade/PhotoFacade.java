package cz.muni.fi.accessiblewebphotogallery.iface.facade;

import cz.muni.fi.accessiblewebphotogallery.iface.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PhotoFacade {

    PageImpl<PhotoDto> findByUploadTimeBetween(Instant begin, Instant end, Pageable pageable);

    PageImpl<PhotoDto> findByUploader(UserDto uploader, Pageable pageable);

    PageImpl<PhotoDto> findByDescPartIgnoreCase(String partialDescription, Pageable pageable);

    PageImpl<PhotoDto> findByTitlePartIgnoreCase(String partialTitle, Pageable pageable);

    Optional<PhotoDto> findByBase64Id(String base64Id);

    PageImpl<PhotoDto> findNewestFirst(Pageable pageable);

    PageImpl<PhotoDto> findMultipleByBase64(List<String> b64ids, Pageable pageable);

    PhotoDto registerPhoto(PhotoDto photo, File photoFile, File metadataFile);

    PhotoDto updatePhoto(PhotoDto photoDto);

    void deletePhoto(PhotoDto photo);

}
