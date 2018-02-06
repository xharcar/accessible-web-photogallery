package cz.muni.fi.accessiblewebphotogallery.iface;

import java.util.Objects;

public class BuildingInfoDto {

    private Long id;

    private Long photoId;

    private int distance;

    private String buildingName;

    private double latitude;

    private double longitude;

    private int minX;

    private int maxX;

    private int minY;

    private int maxY;

    public BuildingInfoDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
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

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BuildingInfoDto)) return false;
        BuildingInfoDto that = (BuildingInfoDto) o;
        return distance == that.distance &&
                Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                Objects.equals(photoId, that.photoId) &&
                Objects.equals(buildingName, that.buildingName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photoId, distance, buildingName, latitude, longitude);
    }

    @Override
    public String toString() {
        return "BuildingInfoDto{" +
                "id=" + id +
                ", photoId=" + photoId +
                ", distance=" + distance +
                ", buildingName='" + buildingName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", minX=" + minX +
                ", maxX=" + maxX +
                ", minY=" + minY +
                ", maxY=" + maxY +
                '}';
    }
}
