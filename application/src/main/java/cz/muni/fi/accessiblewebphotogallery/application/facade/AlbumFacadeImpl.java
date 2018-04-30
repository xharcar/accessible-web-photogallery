package cz.muni.fi.accessiblewebphotogallery.application.facade;

import cz.muni.fi.accessiblewebphotogallery.application.PhotoGalleryBackendMapper;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.AlbumService;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.AlbumDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.AlbumFacade;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AlbumEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AlbumFacadeImpl implements AlbumFacade {

    private AlbumService albumService;

    @Inject
    public AlbumFacadeImpl(AlbumService albumService) {
        this.albumService = albumService;
    }

    @Override
    public List<AlbumDto> findAll() {
        return albumService.findAll().stream().map(this::albumToDto).collect(Collectors.toList());
    }

    @Override
    public List<AlbumDto> findByAlbumOwner(UserDto owner) {
        return albumService.findByAlbumOwner(userDtoToEntity(owner)).stream().map(this::albumToDto).collect(Collectors.toList());
    }

    @Override
    public AlbumDto createAlbum(UserDto user, String albumName) {
        return albumToDto(albumService.createAlbum(userDtoToEntity(user),albumName));
    }

    @Override
    public AlbumDto updateAlbum(AlbumDto album) {
        return albumToDto(albumService.updateAlbum(albumDtoToEntity(album)));
    }

    @Override
    public boolean addPhotoToAlbum(AlbumDto albumDto, String photoB64Id) {
        return albumService.addPhotoToAlbum(albumDtoToEntity(albumDto),photoB64Id);
    }

    @Override
    public boolean removePhotoFromAlbum(AlbumDto albumDto, String photoB64Id) {
        return albumService.removePhotoFromAlbum(albumDtoToEntity(albumDto),photoB64Id);
    }

    @Override
    public List<String> listPhotosInAlbum(AlbumDto albumDto) {
        return albumService.listPhotosInAlbum(albumDtoToEntity(albumDto));
    }

    @Override
    public Optional<AlbumDto> findByBase64Id(String base64) {
        return albumService.findByBase64Id(base64).map(this::albumToDto);
    }

    @Override
    public void deleteAlbum(AlbumDto albumDto) {
        albumService.deleteAlbum(albumDtoToEntity(albumDto));
    }


    private UserEntity userDtoToEntity(UserDto userDto){
        return PhotoGalleryBackendMapper.userDtoToEntity(userDto);
    }

    private AlbumDto albumToDto(AlbumEntity entity){
        return PhotoGalleryBackendMapper.albumEntityToDto(entity);
    }

    private AlbumEntity albumDtoToEntity(AlbumDto dto){
        return PhotoGalleryBackendMapper.albumDtoToEntity(dto);
    }

}
