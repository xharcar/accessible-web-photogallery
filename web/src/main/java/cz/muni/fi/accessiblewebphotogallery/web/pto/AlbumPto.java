package cz.muni.fi.accessiblewebphotogallery.web.pto;

import java.util.Objects;

public class AlbumPto {

    private Long id;

    private UserPto albumOwner;

    private String albumName;

    private String base64Id;

    public AlbumPto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserPto getAlbumOwner() {
        return albumOwner;
    }

    public void setAlbumOwner(UserPto albumOwner) {
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
        if (!(o instanceof AlbumPto)) return false;
        AlbumPto albumPto = (AlbumPto) o;
        return Objects.equals(base64Id, albumPto.base64Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base64Id);
    }

    @Override
    public String toString() {
        return "AlbumPto{" +
                "id=" + id +
                ", albumOwner=" + albumOwner +
                ", albumName='" + albumName + '\'' +
                ", base64Id='" + base64Id + '\'' +
                '}';
    }
}
