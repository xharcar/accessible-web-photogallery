package cz.muni.fi.accessiblewebphotogallery.persistence.dao;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AlbumEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumDao extends JpaRepository<AlbumEntity, String> {

    List<AlbumEntity> findByAlbumOwner(UserEntity albumOwner);

    Optional<AlbumEntity> findById(String id);

}
