package cz.muni.fi.accessiblewebphotogallery.iface.facade;

import cz.muni.fi.accessiblewebphotogallery.iface.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PhotoFacade {

    List<PhotoDto> findByUploadTimeBetween(Instant begin, Instant end);

    List<PhotoDto> findByUploader(UserDto uploader);

    List<PhotoDto> findByDescriptionContaining(String partialDescription);

    List<PhotoDto> findByTitleContaining(String partialTitle);

    Optional<PhotoDto> findByBase64Id(String base64Id);

    List<PhotoDto> findAllMostRecentFirst();

}
