package cz.muni.fi.accessiblewebphotogallery.application.facade;

import cz.muni.fi.accessiblewebphotogallery.application.PhotoGalleryBackendMapper;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.AlbumService;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.AlbumDto;
import cz.muni.fi.accessiblewebphotogallery.facade.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.facade.facade.AlbumFacade;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AlbumEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlbumFacadeImpl implements AlbumFacade {

    private AlbumService albumService;

    @Autowired
    public AlbumFacadeImpl(AlbumService albumService) {
        this.albumService = albumService;
    }

    @Override
    public List<AlbumDto> findAllAlbums() {
        return albumService.findAll().stream().map(PhotoGalleryBackendMapper::albumEntityToDto).collect(Collectors.toList());
    }

    @Override
    public Optional<AlbumDto> findAlbumById(String id) {
        Optional<AlbumEntity> albumEntityOptional = albumService.findById(id);
        if(!albumEntityOptional.isPresent()){
            return Optional.empty();
        }
        return Optional.of(PhotoGalleryBackendMapper.albumEntityToDto(albumEntityOptional.get()));
    }

    @Override
    public List<AlbumDto> findAlbumsByOwner(UserDto owner) {
        return albumService.findByOwner(PhotoGalleryBackendMapper.userDtoToEntity(owner)).stream().map(PhotoGalleryBackendMapper::albumEntityToDto).collect(Collectors.toList());
    }

    @Override
    public AlbumDto createAlbum(UserDto user, String albumName) {
        return PhotoGalleryBackendMapper.albumEntityToDto(albumService.createAlbum(PhotoGalleryBackendMapper.userDtoToEntity(user), albumName));
    }

    @Override
    public AlbumDto updateAlbum(AlbumDto album) {
        return PhotoGalleryBackendMapper.albumEntityToDto(albumService.updateAlbum(PhotoGalleryBackendMapper.albumDtoToEntity(album)));
    }

    @Override
    public boolean addPhotoToAlbum(AlbumDto albumDto, String photoB64Id) {
        return albumService.addPhotoToAlbum(PhotoGalleryBackendMapper.albumDtoToEntity(albumDto), photoB64Id);
    }

    @Override
    public boolean removePhotoFromAlbum(AlbumDto albumDto, String photoB64Id) {
        return albumService.removePhotoFromAlbum(PhotoGalleryBackendMapper.albumDtoToEntity(albumDto), photoB64Id);
    }


    @Override
    public void deleteAlbum(AlbumDto albumDto) {
        albumService.deleteAlbum(PhotoGalleryBackendMapper.albumDtoToEntity(albumDto));
    }

    @Override
    public List<String> listPhotosInAlbum(AlbumDto dto) {
        return albumService.listPhotosInAlbum(PhotoGalleryBackendMapper.albumDtoToEntity(dto));
    }


}
