package cz.muni.fi.accessiblewebphotogallery.persistence.entity;


import javax.persistence.*;
import java.util.Objects;

// Information about a building in a picture
@Entity
@Table(name = "BUILDINGINFO")
public class BuildingInfo {
    private double EPSILON = 0.0000025;// see BuildingInfoServiceImpl

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "photo_id", nullable = false)
    private PhotoEntity photo;

    @Column(nullable = false)
    private Long osmId = -1L; // -1L serves as "unknown/not found"
    // OSM uses 64-bit (presumably signed) integers for IDs

    @Column(nullable = false)
    private int distance;

    @Column(nullable = true, length = 192)
    // According to Guinness World Records, the longest place name is the official Thai name for Bangkok,
    // transcribed into Latin script, at 176~187 incl. spaces - therefore 192 characters should do
    // (as long as nobody breaks it- to whom it may concern: please don't)
    private String buildingName;

    /*
    It makes no sense for a building not to have its set of GPS coordinates;
    hence it makes sense for these to be non-nullable.
    However, if the info is unavailable for whatever reason,
    Null Island (GPS coordinates {0.0;0.0} will have to do...
    */
    @Column(nullable = false, precision = 7)// according to OSM wiki, 7 digits of precision are used
    private double latitude;

    @Column(nullable = false, precision = 7)
    private double longitude;

    //minX, maxX - approximate horizontal bounds of a building in the photo
    @Column(nullable = false)
    private int photoMinX;

    @Column(nullable = false)
    private int photoMaxX;

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

    public Long getOsmId() {
        return osmId;
    }

    public void setOsmId(Long osmId) {
        this.osmId = osmId;
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

    public String getFocusText() {
        return focusText;
    }

    public void setFocusText(String focusText) {
        this.focusText = focusText;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if (this == o) return true;
        if (!(o instanceof BuildingInfo)) return false;
        BuildingInfo that = (BuildingInfo) o;
        return  osmId.equals(that.osmId) ||
                (Math.abs(Double.compare(that.latitude, latitude)) <= EPSILON &&
                Math.abs(Double.compare(that.longitude, longitude)) <= EPSILON);
        // a single building can be in multiple photos, known under multiple names, and be photographed from different distances;
        // if a BuildingInfo instance has the same OSM ID as another, then they're of the same building;
        // if that fails (eg. OSM doesn't have that building in its DB), it's pretty safe to assume no two buildings are
        // so small and close enough that their latitude/longitude differ by less than EPSILON
    }

    @Override
    public int hashCode() {
        return Objects.hash(osmId, latitude, longitude);
    }

    @Override
    public String toString() {
        return "BuildingInfo{" +
                "id=" + id +
                ", photo=" + photo +
                ", osmId=" + osmId +
                ", distance=" + distance +
                ", buildingName='" + buildingName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", photoMinX=" + photoMinX +
                ", photoMaxX=" + photoMaxX +
                '}';
    }
}
