package cz.muni.fi.accessiblewebphotogallery.persistence.dao;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AlbumEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumDao extends JpaRepository<AlbumEntity,Long> {

    List<AlbumEntity> findByAlbumOwner(UserEntity albumOwner);

    Optional<AlbumEntity> findByBase64Identifier(String base64);

//    List<AlbumEntity> findByAlbumName(String albumName);
//
//    List<AlbumEntity> findByAlbumNameContainingIgnoreCase(String partialAlbumName);

}
