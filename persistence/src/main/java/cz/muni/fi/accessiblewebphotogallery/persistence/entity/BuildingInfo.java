package cz.muni.fi.accessiblewebphotogallery.persistence.entity;


import javax.persistence.*;
import java.util.Objects;

// Information about a building in a picture
@Entity
@Table(name = "BUILDINGINFO")
public class BuildingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "photo_id", nullable = false)
    private PhotoEntity photo;

    @Column(nullable = false)
    private int distance;

    @Column(nullable = true)
    private String buildingName;

    @Column(nullable = false, precision = 10)
    private double latitude;

    @Column(nullable = false, precision = 10)
    private double longitude;

    //minX, minY, maxX, maxY - approximate bounds of a building in the photo
    @Column(nullable = false)
    private int photoMinX;

    @Column(nullable = false)
    private int photoMaxX;

    @Column(nullable = false)
    private int photoMinY;

    @Column(nullable = false)
    private int photoMaxY;

    // text to be displayed on mouseover/focus
    @Column(nullable = true, length = 384)
    private String focusText;

    public BuildingInfo() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PhotoEntity getPhoto() {
        return photo;
    }

    public void setPhoto(PhotoEntity photo) {
        this.photo = photo;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getPhotoMinX() {
        return photoMinX;
    }

    public void setPhotoMinX(int photoMinX) {
        this.photoMinX = photoMinX;
    }

    public int getPhotoMaxX() {
        return photoMaxX;
    }

    public void setPhotoMaxX(int photoMaxX) {
        this.photoMaxX = photoMaxX;
    }

    public int getPhotoMinY() {
        return photoMinY;
    }

    public void setPhotoMinY(int photoMinY) {
        this.photoMinY = photoMinY;
    }

    public int getPhotoMaxY() {
        return photoMaxY;
    }

    public void setPhotoMaxY(int photoMaxY) {
        this.photoMaxY = photoMaxY;
    }

    public String getFocusText() {
        return focusText;
    }

    public void setFocusText(String focusText) {
        this.focusText = focusText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BuildingInfo)) return false;
        BuildingInfo that = (BuildingInfo) o;
        return distance == that.distance &&
                Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                Objects.equals(photo, that.photo) &&
                Objects.equals(buildingName, that.buildingName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photo, distance, buildingName, latitude, longitude);
    }

    @Override
    public String toString() {
        return "BuildingInfo{" +
                "id=" + id +
                ", photo=" + photo +
                ", distance=" + distance +
                ", buildingName='" + buildingName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", photoMinX=" + photoMinX +
                ", photoMaxX=" + photoMaxX +
                ", photoMinY=" + photoMinY +
                ", photoMaxY=" + photoMaxY +
                '}';
    }
}
