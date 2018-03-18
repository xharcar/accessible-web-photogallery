package cz.muni.fi.accessiblewebphotogallery.persistence.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "ALBUMS")
public class AlbumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "uploader_id", nullable = false)
    private UserEntity albumOwner;

    @Column(length = 128) // if photos can have 128 characters...
    private String albumName;

    public AlbumEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getAlbumOwner() {
        return albumOwner;
    }

    public void setAlbumOwner(UserEntity albumOwner) {
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
        if (!(o instanceof AlbumEntity)) return false;
        AlbumEntity that = (AlbumEntity) o;
        return Objects.equals(albumOwner, that.albumOwner) &&
                Objects.equals(albumName, that.albumName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(albumOwner, albumName);
    }
}
