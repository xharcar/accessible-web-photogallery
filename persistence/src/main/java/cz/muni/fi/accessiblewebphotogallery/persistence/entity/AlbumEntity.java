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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "uploader_id", nullable = false)
    private UserEntity albumOwner;

    @Column(length = 128) // if photos can have 128 characters...
    private String albumName;

    @Column(length = 24, unique = true, nullable = false)
    private String base64Identifier;

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

    public String getBase64Identifier() {
        return base64Identifier;
    }

    public void setBase64Identifier(String base64Identifier) {
        this.base64Identifier = base64Identifier;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if (this == o) return true;
        if (!(o instanceof AlbumEntity)) return false;
        AlbumEntity that = (AlbumEntity) o;
        return base64Identifier.equals(that.base64Identifier);
    }

    @Override
    public int hashCode() {
        return base64Identifier.hashCode();
    }

    @Override
    public String toString() {
        return "AlbumEntity{" +
                "id=" + id +
                ", albumOwner=" + albumOwner +
                ", albumName='" + albumName + '\'' +
                ", base64Identifier='" + base64Identifier + '\'' +
                '}';
    }
}
