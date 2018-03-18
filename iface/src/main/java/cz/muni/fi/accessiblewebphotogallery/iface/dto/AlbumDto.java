package cz.muni.fi.accessiblewebphotogallery.iface.dto;

import java.util.Objects;

public class AlbumDto {

    private Long id;

    private UserDto albumOwner;

    private String albumName;

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

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if (this == o) return true;
        if (!(o instanceof AlbumDto)) return false;
        AlbumDto albumDto = (AlbumDto) o;
        return Objects.equals(albumOwner, albumDto.albumOwner) &&
                Objects.equals(albumName, albumDto.albumName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(albumOwner, albumName);
    }

    @Override
    public String toString() {
        return "AlbumDto{" +
                "id=" + id +
                ", albumOwner=" + albumOwner +
                ", albumName='" + albumName + '\'' +
                '}';
    }
}
