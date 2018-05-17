package cz.muni.fi.accessiblewebphotogallery.facade.facade;

import cz.muni.fi.accessiblewebphotogallery.facade.dto.AlbumDto;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface AlbumFacade {

    List<AlbumDto> findAll();

    List<AlbumDto> findByAlbumOwner(UserDto owner);

    AlbumDto createAlbum(UserDto user, String albumName);

    AlbumDto updateAlbum(AlbumDto album);

    void deleteAlbum(AlbumDto albumDto);

    boolean addPhotoToAlbum(AlbumDto albumDto, String photoB64Id);

    boolean removePhotoFromAlbum(AlbumDto albumDto, String photoB64Id);

    List<String> listPhotosInAlbum(AlbumDto albumDto);// returns b64 IDs

    Optional<AlbumDto> findById(String base64);

}