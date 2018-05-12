package cz.muni.fi.accessiblewebphotogallery.web.pto;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

public class PhotoPto {

    private Long id;

    @NotNull
    private UserPto uploader;

    @Past
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant uploadTime;

    @NotBlank
    private String title;

    @NotNull
    private String description;

    @NotBlank
    private String base64Id;

    private Double cameraLatitude;

    private Double cameraLongitude;

    private Double cameraAzimuth;

    private Double positionAccuracy;

    private Double cameraFOV;

    @Past
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime datetimeTaken;

    private String cameraModel;

    private Integer imageWidth;

    private Integer imageHeight;

    private Integer iso;

    private Boolean flash;

    private Double exposureTime;

    public PhotoPto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserPto getUploader() {
        return uploader;
    }

    public void setUploader(UserPto uploader) {
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

    public String getBase64Id() {
        return base64Id;
    }

    public void setBase64Id(String base64Id) {
        this.base64Id = base64Id;
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

    public Double getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(Double exposureTime) {
        this.exposureTime = exposureTime;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if (this == o) return true;
        if (!(o instanceof PhotoPto)) return false;
        PhotoPto photoPto = (PhotoPto) o;
        return Objects.equals(base64Id, photoPto.base64Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base64Id);
    }

    @Override
    public String toString() {
        return "PhotoPto{" +
                "id=" + id +
                ", uploader=" + uploader +
                ", uploadTime=" + uploadTime +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", base64Id='" + base64Id + '\'' +
                ", cameraLatitude=" + cameraLatitude +
                ", cameraLongitude=" + cameraLongitude +
                ", cameraAzimuth=" + cameraAzimuth +
                ", positionAccuracy=" + positionAccuracy +
                ", cameraFOV=" + cameraFOV +
                ", datetimeTaken=" + datetimeTaken +
                ", cameraModel='" + cameraModel + '\'' +
                ", imageWidth=" + imageWidth +
                ", imageHeight=" + imageHeight +
                ", iso=" + iso +
                ", flash=" + flash +
                ", exposureTime=" + exposureTime +
                '}';
    }
}