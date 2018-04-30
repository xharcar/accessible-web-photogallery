package cz.muni.fi.accessiblewebphotogallery.persistence.entity;

import javax.persistence.*;

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

    @Column(length = 24, unique = true, nullable = false)
    private String base64Id;

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

    public String getBase64Id() {
        return base64Id;
    }

    public void setBase64Id(String base64Identifier) {
        this.base64Id = base64Identifier;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if (this == o) return true;
        if (!(o instanceof AlbumEntity)) return false;
        AlbumEntity that = (AlbumEntity) o;
        return base64Id.equals(that.base64Id);
    }

    @Override
    public int hashCode() {
        return base64Id.hashCode();
    }

    @Override
    public String toString() {
        return "AlbumEntity{" +
                "id=" + id +
                ", albumOwner=" + albumOwner +
                ", albumName='" + albumName + '\'' +
                ", base64Identifier='" + base64Id + '\'' +
                '}';
    }
}
