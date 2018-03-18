package cz.muni.fi.accessiblewebphotogallery.iface.facade;

import cz.muni.fi.accessiblewebphotogallery.iface.dto.AlbumDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AlbumEntity;

import java.util.List;

public interface AlbumFacade {

    List<AlbumDto> findByAlbumOwner(UserDto owner);

    List<AlbumDto> findByAlbumName(String albumName);

    List<AlbumEntity> findByAlbumNameContainingIgnoreCase(String partialName);


}
