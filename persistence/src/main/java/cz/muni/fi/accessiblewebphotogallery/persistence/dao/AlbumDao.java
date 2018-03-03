package cz.muni.fi.accessiblewebphotogallery.persistence.dao;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AlbumEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumDao extends JpaRepository<AlbumEntity,Long> {

    List<AlbumEntity> findByAlbumOwner(UserEntity albumOwner);

    List<AlbumEntity> findByAlbumName(String albumName);

    List<AlbumEntity> findByAlbumNameContaining(String partialAlbumName);

}
