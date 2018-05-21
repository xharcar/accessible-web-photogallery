package cz.muni.fi.accessiblewebphotogallery.proxy;

import cz.muni.fi.accessiblewebphotogallery.facade.dto.BuildingInfoDto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class PhotoProxy {

    private String id;
    private UserProxy uploader;
    private Instant uploadTime;
    private String title;
    private String description;
    private Double cameraLatitude;
    private Double cameraLongitude;
    private Double cameraAzimuth;
    private Double positionAccuracy;
    private Double cameraFOV;
    private Double exposureTime;
    private LocalDateTime datetimeTaken;
    private String cameraModel;
    private Integer imageWidth;
    private Integer imageHeight;
    private Integer iso;
    private Boolean flash;
    private List<BuildingInfoDto> buildingList;

    public PhotoProxy() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserProxy getUploader() {
        return uploader;
    }

    public void setUploader(UserProxy uploader) {
        this.uploader = uploader;
    }

    public Instant getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Instant uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCameraLatitude() {
        return cameraLatitude;
    }

    public void setCameraLatitude(Double cameraLatitude) {
        this.cameraLatitude = cameraLatitude;
    }

    public Double getCameraLongitude() {
        return cameraLongitude;
    }

    public void setCameraLongitude(Double cameraLongitude) {
        this.cameraLongitude = cameraLongitude;
    }

    public Double getCameraAzimuth() {
        return cameraAzimuth;
    }

    public void setCameraAzimuth(Double cameraAzimuth) {
        this.cameraAzimuth = cameraAzimuth;
    }

    public Double getPositionAccuracy() {
        return positionAccuracy;
    }

    public void setPositionAccuracy(Double positionAccuracy) {
        this.positionAccuracy = positionAccuracy;
    }

    public Double getCameraFOV() {
        return cameraFOV;
    }

    public void setCameraFOV(Double cameraFOV) {
        this.cameraFOV = cameraFOV;
    }

    public Double getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(Double exposureTime) {
        this.exposureTime = exposureTime;
    }

    public LocalDateTime getDatetimeTaken() {
        return datetimeTaken;
    }

    public void setDatetimeTaken(LocalDateTime datetimeTaken) {
        this.datetimeTaken = datetimeTaken;
    }

    public String getCameraModel() {
        return cameraModel;
    }

    public void setCameraModel(String cameraModel) {
        this.cameraModel = cameraModel;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }

    public Integer getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }

    public Integer getIso() {
        return iso;
    }

    public void setIso(Integer iso) {
        this.iso = iso;
    }

    public Boolean getFlash() {
        return flash;
    }

    public void setFlash(Boolean flash) {
        this.flash = flash;
    }

    public List<BuildingInfoDto> getBuildingList() {
        return buildingList;
    }

    public void setBuildingList(List<BuildingInfoDto> buildingList) {
        this.buildingList = buildingList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof PhotoProxy)) return false;
        PhotoProxy that = (PhotoProxy) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PhotoProxy{" +
                "id='" + id + '\'' +
                ", uploader=" + uploader +
                ", uploadTime=" + uploadTime +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", cameraLatitude=" + cameraLatitude +
                ", cameraLongitude=" + cameraLongitude +
                ", cameraAzimuth=" + cameraAzimuth +
                ", positionAccuracy=" + positionAccuracy +
                ", cameraFOV=" + cameraFOV +
                ", exposureTime=" + exposureTime +
                ", datetimeTaken=" + datetimeTaken +
                ", cameraModel='" + cameraModel + '\'' +
                ", imageWidth=" + imageWidth +
                ", imageHeight=" + imageHeight +
                ", iso=" + iso +
                ", flash=" + flash +
                ", buildingList=" + buildingList +
                '}';
    }
}
