package cz.muni.fi.accessiblewebphotogallery.application.service.iface;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AlbumEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface AlbumService {

    List<AlbumEntity> findAll();

    List<AlbumEntity> findByAlbumOwner(UserEntity owner);

    AlbumEntity createAlbum(UserEntity user, String albumName);

    AlbumEntity saveAlbum(AlbumEntity album);

    void deleteAlbum(AlbumEntity album);

    boolean addPhotoToAlbum(AlbumEntity album, String photoBase64Id);

    boolean removePhotoFromAlbum(AlbumEntity album, String photoBase64Id);

    List<String> listPhotosInAlbum(AlbumEntity album);

    Optional<AlbumEntity> findByBase64Identifier(String base64);
}
