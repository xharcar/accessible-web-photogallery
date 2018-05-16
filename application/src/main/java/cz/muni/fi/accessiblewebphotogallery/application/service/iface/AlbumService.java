package cz.muni.fi.accessiblewebphotogallery.application.service.iface;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AlbumEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface AlbumService {

    /**
     * Retrieves all album entries
     *
     * @return List containing all album entries
     */
    List<AlbumEntity> findAll();

    /**
     * Retrieves album entries belonging to a user
     *
     * @param owner user to look up
     * @return List containig all album entries created by owner
     */
    List<AlbumEntity> findByAlbumOwner(UserEntity owner);

    /**
     * Creates a new album entry
     *
     * @param user      User creating a new album
     * @param albumName Name given to the new album by the user
     * @return Album entry with the given name, a Base-64 identifier, as saved in the DB
     */
    AlbumEntity createAlbum(UserEntity user, String albumName);

    /**
     * Updates an album (name)
     *
     * @param album Album (whose name) to update
     * @return Updated album
     */
    AlbumEntity updateAlbum(AlbumEntity album);

    /**
     * Removes an album entry from the DB
     *
     * @param album Album entry to be removed
     */
    void deleteAlbum(AlbumEntity album);

    /**
     * Adds a photo to an album
     *
     * @param album         Album to which the photo is to be added
     * @param photoId Base-64 identifier of the too-be-added photo
     * @return true if successful, false upon failure
     */
    boolean addPhotoToAlbum(AlbumEntity album, String photoId);

    /**
     * Removes a photo from an album, if it's there
     *
     * @param album         Album from which to remove the photo
     * @param photoId Base-64 identifier of the to-be-removed photo
     * @return true upon success, false upon failure
     */
    boolean removePhotoFromAlbum(AlbumEntity album, String photoId);

    /**
     * Lists all the photos belonging to an album
     *
     * @param album Album whose photos are to be listed
     * @return List containing Base-64 identifiers ofa photos belonging to album
     */
    List<String> listPhotosInAlbum(AlbumEntity album);

    /**
     * Retrieves an album by its Base-64 identifier
     *
     * @param base64 Base-64 identifier to look up
     * @return Optional containing an AlbumEntity whose Id equals base64 if such is found, an empty Optional otherwise
     */
    Optional<AlbumEntity> findById(String base64);

}
