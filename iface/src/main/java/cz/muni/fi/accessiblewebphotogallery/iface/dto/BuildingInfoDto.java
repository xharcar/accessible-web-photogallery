package cz.muni.fi.accessiblewebphotogallery.iface.dto;

import java.util.Objects;

public class BuildingInfoDto {

    private Long id;

    private PhotoDto photo;

    private Long osmId;

    private Integer distance;

    private String buildingName;

    private Double latitude;

    private Double longitude;

    private Integer minX;

    private Integer maxX;

    private String focusText;

    public BuildingInfoDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PhotoDto getPhoto() {
        return photo;
    }

    public void setPhoto(PhotoDto photo) {
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

    public Integer getMinX() {
        return minX;
    }

    public void setMinX(Integer minX) {
        this.minX = minX;
    }

    public Integer getMaxX() {
        return maxX;
    }

    public void setMaxX(Integer maxX) {
        this.maxX = maxX;
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
        if (!(o instanceof BuildingInfoDto)) return false;
        BuildingInfoDto that = (BuildingInfoDto) o;
        return distance == that.distance &&
                Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                Objects.equals(photo, that.photo) &&
                Objects.equals(buildingName, that.buildingName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(osmId, latitude, longitude);
    }

    @Override
    public String toString() {
        return "BuildingInfoDto{" +
                "id=" + id +
                ", photo=" + photo +
                ", distance=" + distance +
                ", buildingName='" + buildingName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", minX=" + minX +
                ", maxX=" + maxX +
                ", focusText='" + focusText + '\'' +
                '}';
    }
}
