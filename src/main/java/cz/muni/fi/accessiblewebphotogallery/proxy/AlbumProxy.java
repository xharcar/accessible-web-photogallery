package cz.muni.fi.accessiblewebphotogallery.proxy;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class AlbumProxy {

    private String id;
    private UserProxy owner;

    @NotBlank
    private String name;

    public AlbumProxy() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserProxy getOwner() {
        return owner;
    }

    public void setOwner(UserProxy owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlbumProxy)) return false;
        AlbumProxy that = (AlbumProxy) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AlbumProxy{" +
                "id='" + id + '\'' +
                ", owner=" + owner +
                ", name='" + name + '\'' +
                '}';
    }
}
