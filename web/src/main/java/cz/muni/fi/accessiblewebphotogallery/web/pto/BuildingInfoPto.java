package cz.muni.fi.accessiblewebphotogallery.web.pto;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class BuildingInfoPto {

    private double EPSILON = 0.0000025;// see BuildingInfoServiceImpl

    private Long id;

    @NotNull
    private PhotoPto photo;

    @NotNull
    private Long osmId;

    private Integer distance;

    private String buildingName;

    private Double latitude;

    private Double longitude;

    private Integer photoMinX;

    private Integer photoMaxX;

    private String focusText;

    public BuildingInfoPto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PhotoPto getPhoto() {
        return photo;
    }

    public void setPhoto(PhotoPto photo) {
        this.photo = photo;
    }

    public Long getOsmId() {
        return osmId;
    }

    public void setOsmId(Long osmId) {
        this.osmId = osmId;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getPhotoMinX() {
        return photoMinX;
    }

    public void setPhotoMinX(Integer photoMinX) {
        this.photoMinX = photoMinX;
    }

    public Integer getPhotoMaxX() {
        return photoMaxX;
    }

    public void setPhotoMaxX(Integer photoMaxX) {
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
        if (this == o) return true;
        if (!(o instanceof BuildingInfoPto)) return false;
        BuildingInfoPto that = (BuildingInfoPto) o;
        return Objects.equals(osmId, that.osmId) ||
                (Math.abs(Double.compare(that.latitude, latitude)) <= EPSILON &&
                        Math.abs(Double.compare(that.longitude, longitude)) <= EPSILON);
    }

    @Override
    public int hashCode() {

        return Objects.hash(osmId, latitude, longitude);
    }
}
