package cz.muni.fi.accessiblewebphotogallery.facade.facade;

import cz.muni.fi.accessiblewebphotogallery.facade.dto.AlbumDto;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.UserDto;

import java.util.List;

public interface AlbumFacade{

    List<AlbumDto> findAllAlbums();

    List<AlbumDto> findAlbumsByOwner(UserDto owner);

    AlbumDto createAlbum(UserDto user, String albumName);

    AlbumDto updateAlbum(AlbumDto album);

    void deleteAlbum(AlbumDto albumDto);

    boolean addPhotoToAlbum(AlbumDto albumDto, String photoB64Id);

    boolean removePhotoFromAlbum(AlbumDto albumDto, String photoB64Id);


}
