package cz.muni.fi.accessiblewebphotogallery.iface.dto;

import java.util.Objects;

public class AlbumDto {

    private Long id;

    private UserDto albumOwner;

    private String albumName;

    private String base64Id;

    public AlbumDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDto getAlbumOwner() {
        return albumOwner;
    }

    public void setAlbumOwner(UserDto albumOwner) {
        this.albumOwner = albumOwner;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getBase64Id() {
        return base64Id;
    }

    public void setBase64Id(String base64Id) {
        this.base64Id = base64Id;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if (this == o) return true;
        if (!(o instanceof AlbumDto)) return false;
        AlbumDto albumDto = (AlbumDto) o;
        return base64Id.equals(albumDto.getBase64Id());
    }

    @Override
    public int hashCode() {
        return Objects.hash(base64Id);
    }

    @Override
    public String toString() {
        return "AlbumDto{" +
                "id=" + id +
                ", albumOwner=" + albumOwner +
                ", albumName='" + albumName + '\'' +
                ", base64Id='" + base64Id + '\'' +
                '}';
    }
}
