package cz.muni.fi.accessiblewebphotogallery.facade.dto;

import java.util.Objects;

public class AlbumDto {

    private String id;

    private UserDto owner;

    private String albumName;

    public AlbumDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserDto getOwner() {
        return owner;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (!(o instanceof AlbumDto)) return false;
        AlbumDto albumDto = (AlbumDto) o;
        return id.equals(albumDto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AlbumDto{" +
                "id=" + id +
                ", owner=" + owner +
                ", albumName='" + albumName + '\'' +
                '}';
    }
}
