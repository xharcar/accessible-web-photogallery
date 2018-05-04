package cz.muni.fi.accessiblewebphotogallery.application.service.iface;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.time.Instant;
import java.util.Optional;

/**
 * Interacts directly with PhotoDao, does most of the work, such as creating base-64 IDs for photos
 */
public interface PhotoService {

    /**
     * Retrieves photos uploaded within a given time period, inclusive as per Hibernate documentation
     * @param begin The earliest a photo could have been uploaded and still be retrieved
     * @param end The latest a photo could have been uploaded and still be retrieved
     * @param pageable Pageable instance containing the required pagination information
     * @return A PageImpl instance containing up to $limit PhotoEntity instances with begin <= upload times <= end
     */
    PageImpl<PhotoEntity> findByUploadTimeBetween(Instant begin, Instant end, Pageable pageable);

    /**
     * Retrieves photos uploaded by a user
     * @param uploader User whose photos are to be retrieved
     * @param pageable Pageable instance containing the required pagination information
     * @return A PageImpl instance containing up to $limit PhotoEntity instances representing photos uploaded by uploader
     */
    PageImpl<PhotoEntity> findByUploader(UserEntity uploader, Pageable pageable);

    /**
     * Retrieves photos whose description contains a given string, case-insensitive
     * @param searchStr String to search
     * @param pageable Pageable instance containing the required pagination information
     * @return A PageImpl instance containing up to $limit PhotoEntity instances whose descriptions match searchStr
     */
    PageImpl<PhotoEntity> findByDescriptionApx(String searchStr, Pageable pageable);

    /**
     * Retrieves photos whose title contains a given string, case-insensitive
     * @param searchStr String to search
     * @param pageable Pageable instance containing the required pagination information
     * @return A PageImpl instance containing up to $limit PhotoEntity instances whose titles match searchStr
     */
    PageImpl<PhotoEntity> findByTitleApx(String searchStr, Pageable pageable);

    /**
     * Finds photo by ID
     * @param id DB ID to look up
     * @return Optional with a photo whose DB ID == id, empty Optional otherwise
     */
    Optional<PhotoEntity> findById(Long id);

    /**
     * Retrieves a photo with a given Base-64 identifier
     * @param b64id Base-64 identifier to search
     * @return Optional containing a PhotoEntity whose base64Identifier equals b64id
     */
    Optional<PhotoEntity> findByBase64Id(String b64id);

    /**
     * Retrieves all photos, sorted by upload time descending
     * @param pageable Pageable instance containing the required pagination information
     * @return A PageImpl containing up to $limit PhotoEntity instances, sorted by upload time in descending order
     * (ie. most recent first)
     */
    PageImpl<PhotoEntity> findNewestFirst(Pageable pageable);

    /**
     * Registers a PhotoEntity in the DB
     * @param entity PhotoEntity, with some basic info, to save
     * @param photoFile File instance referring to the photo itself
     * @param metadataFile File instance referring to the supplementary JSON metadata file, if provided
     * @return PhotoEntity as registered, with EXIF data and suppplementary data from the JSON file, if available
     */
    PhotoEntity registerPhoto(PhotoEntity entity, File photoFile, File metadataFile);

    /**
     * Updates an already-existing photo
     * @param photo Photo to update with new data
     * @return PhotoEntity as saved
     */
    PhotoEntity updatePhoto(PhotoEntity photo);

    /**
     * Removes a photo from the DB.
     * @param photo PhotoEntity to be removed
     */
    void deletePhoto(PhotoEntity photo);

}
