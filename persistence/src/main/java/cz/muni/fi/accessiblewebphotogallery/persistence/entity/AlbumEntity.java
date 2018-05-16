package cz.muni.fi.accessiblewebphotogallery.persistence.entity;

import javax.persistence.*;

/**
 * Entity representing an album, ie. a list of photos. The actual list is stored in a text file separate from the main
 * database to allow for arbitrary-size albums. The base-64 identifier also serves as the name of the text file storing
 * the album; since changing the album name doesn't change the base-64 ID, the name can be changed at will without
 * the need to do anything else.
 * For browsing, the URL idea is:
 * (photogallery address)/(photo base-64 ID)&album=(album base-64 ID)&index=(photo number in album)
 * Inspiration taken from YouTube, which seems to use a similar system.
 */
@Entity
@Table(name = "ALBUMS")
public class AlbumEntity {

    @Id
    @Column(length = 24, unique = true, nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "uploader_id", nullable = false)
    private UserEntity albumOwner;

    @Column(length = 128) // if photos can have 128 characters...
    private String albumName;

    public AlbumEntity() {
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

    public String getId() {
        return id;
    }

    public void setId(String Id) {
        this.id = Id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (!(o instanceof AlbumEntity)) return false;
        AlbumEntity that = (AlbumEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "AlbumEntity{" +
                "id=" + id +
                ", albumOwner=" + albumOwner +
                ", albumName='" + albumName + '\'' +
                '}';
    }
}
