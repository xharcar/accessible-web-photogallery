package cz.muni.fi.accessiblewebphotogallery.facade.dto;

import java.util.Objects;

public class AlbumDto {

    private String id;

    private UserDto albumOwner;

    private String albumName;

    private String Id;

    public AlbumDto() {
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

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (!(o instanceof AlbumDto)) return false;
        AlbumDto albumDto = (AlbumDto) o;
        return Id.equals(albumDto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id);
    }

    @Override
    public String toString() {
        return "AlbumDto{" +
                "id=" + id +
                ", albumOwner=" + albumOwner +
                ", albumName='" + albumName + '\'' +
                ", Id='" + Id + '\'' +
                '}';
    }
}
