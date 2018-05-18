package cz.muni.fi.accessiblewebphotogallery.application.service.iface;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Interacts directly with PhotoDao, does most of the work, such as creating base-64 IDs for photos
 */
public interface PhotoService {

    /**
     * Retrieves photos uploaded within a given time period, inclusive as per Hibernate documentation
     *
     * @param begin    The earliest a photo could have been uploaded and still be retrieved
     * @param end      The latest a photo could have been uploaded and still be retrieved
     * @param pageable Pageable instance containing the required pagination information
     * @return A PageImpl instance containing up to $limit PhotoEntity instances with begin <= upload times <= end
     */
    PageImpl<PhotoEntity> findByUploadTimeBetween(Instant begin, Instant end, Pageable pageable);

    /**
     * Retrieves photos uploaded by a user
     *
     * @param uploader User whose photos are to be retrieved
     * @param pageable Pageable instance containing the required pagination information
     * @return A PageImpl instance containing up to $limit PhotoEntity instances representing photos uploaded by uploader
     */
    PageImpl<PhotoEntity> findByUploader(UserEntity uploader, Pageable pageable);

    /**
     * Retrieves photos whose description contains a given string, case-insensitive
     *
     * @param searchStr String to search
     * @param pageable  Pageable instance containing the required pagination information
     * @return A PageImpl instance containing up to $limit PhotoEntity instances whose descriptions match searchStr
     */
    PageImpl<PhotoEntity> findByDescriptionApx(String searchStr, Pageable pageable);

    /**
     * Retrieves photos whose title contains a given string, case-insensitive
     *
     * @param searchStr String to search
     * @param pageable  Pageable instance containing the required pagination information
     * @return A PageImpl instance containing up to $limit PhotoEntity instances whose titles match searchStr
     */
    PageImpl<PhotoEntity> findByTitleApx(String searchStr, Pageable pageable);


    /**
     * Retrieves a photo with a given Base-64 identifier
     *
     * @param b64id Base-64 identifier to search
     * @return Optional containing a PhotoEntity whose Id equals b64id
     */
    Optional<PhotoEntity> findById(String b64id);

    /**
     * Convenience method for retrieving multiple photos at the same time
     * (should be optimizable into one query in theory)
     * @param idList List of photo identifiers we're looking for
     * @return List containing all the photos that were found with identifiers contained in idList
     */
    List<PhotoEntity> findMultipleById(List<String> idList);

    /**
     * Retrieves all photos, sorted by upload time descending
     *
     * @param pageable Pageable instance containing the required pagination information
     * @return A PageImpl containing up to $limit PhotoEntity instances, sorted by upload time in descending order
     * (ie. most recent first)
     */
    PageImpl<PhotoEntity> findNewestFirst(Pageable pageable);

    /**
     * Retrieves the photo uploaded after the given one
     * @param photo photo whose upload successor we want
     * @return Optional with photo uploaded right after photo, or empty if none such exists (ie. photo is the latest)
     */
    Optional<PhotoEntity> findNextUploaded(PhotoEntity photo);

    /**
     * Retrieves the photo uploaded before the given one
     * @param photo photo whose upload predecessor we want
     * @return Optional with photo uploaded right before photo, or empty if none such exists (ie. photo is the first upload)
     */
    Optional<PhotoEntity> findPreviouslyUploaded(PhotoEntity photo);

    /**
     * Registers a PhotoEntity in the DB
     *
     * @param entity       PhotoEntity, with some basic info, to save
     * @param photoFile    File instance referring to the photo itself
     * @param metadataFile File instance referring to the supplementary JSON metadata file, if provided
     * @return PhotoEntity as registered, with EXIF data and suppplementary data from the JSON file, if available
     */
    PhotoEntity registerPhoto(PhotoEntity entity, File photoFile, File metadataFile);

    /**
     * Updates an already-existing photo
     *
     * @param photo Photo to update with new data
     * @return PhotoEntity as saved
     */
    PhotoEntity updatePhoto(PhotoEntity photo);

    /**
     * Clears a photo, ie. removes all information about it, and deletes files belonging to it
     * (ie. the image file itself, the thumbnail, and the supplementary JSON file, if one was uploaded alongside it)
     *
     * @param photo PhotoEntity to be removed
     * @return true upon success, false upon failure (to eg. delete relevant files)
     */
    boolean clearPhoto(PhotoEntity photo);

    void deletePhoto(PhotoEntity photo);

}
