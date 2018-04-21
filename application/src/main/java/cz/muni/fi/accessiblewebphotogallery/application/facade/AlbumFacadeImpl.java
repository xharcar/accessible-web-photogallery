package cz.muni.fi.accessiblewebphotogallery.application.facade;

import cz.muni.fi.accessiblewebphotogallery.application.ApplicationConfig;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.AlbumService;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.AlbumDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.AlbumFacade;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.AlbumEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.dozer.Mapper;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class AlbumFacadeImpl implements AlbumFacade {

    private Mapper mapper;
    private AlbumService albumService;

    @Inject
    public AlbumFacadeImpl(Mapper mapper, AlbumService albumService, ApplicationConfig config) {
        this.mapper = mapper;
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
    public void deleteAlbum(AlbumDto albumDto) {
        albumService.deleteAlbum(albumDtoToEntity(albumDto));
    }


    private UserEntity userDtoToEntity(UserDto userDto){
        return mapper.map(userDto,UserEntity.class);
    }

    private AlbumDto albumToDto(AlbumEntity entity){
        return mapper.map(entity,AlbumDto.class);
    }

    private AlbumEntity albumDtoToEntity(AlbumDto dto){
        return mapper.map(dto,AlbumEntity.class);
    }

}
