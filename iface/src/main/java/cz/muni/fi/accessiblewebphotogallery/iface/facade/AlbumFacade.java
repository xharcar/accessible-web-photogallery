package cz.muni.fi.accessiblewebphotogallery.iface.facade;

import cz.muni.fi.accessiblewebphotogallery.iface.dto.AlbumDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;

import java.util.List;

public interface AlbumFacade {

    List<AlbumDto> findAll();

    List<AlbumDto> findByAlbumOwner(UserDto owner);

    AlbumDto createAlbum(UserDto user, String albumName);

    boolean addPhotoToAlbum(AlbumDto albumDto, String photoB64Id);

    boolean removePhotoFromAlbum(AlbumDto albumDto, String photoB64Id);

    List<String> listPhotosInAlbum(AlbumDto albumDto);// returns b64 IDs

    void deleteAlbum(AlbumDto albumDto);


//    List<AlbumDto> findByAlbumName(String albumName);
//
//    List<AlbumEntity> findByAlbumNameContainingIgnoreCase(String partialName);


}
