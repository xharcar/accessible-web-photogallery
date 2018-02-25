package cz.muni.fi.accessiblewebphotogallery.iface.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

public class PhotoDto {

    private Long id;

    private UserDto uploader;

    private Instant uploadTime;

    private String title;

    private String description;

    private String base64Id;

    private double camLatitude;

    private double camLongitude;

    private double camAzimuth;

    private double posAccuracy;

    private double camFOV;

    private LocalDateTime datetimeTaken;

    private String camModel;

    private int width;

    private int height;

    private int iso;

    private boolean flash;

    private double expTime;

    public PhotoDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDto getUploader() {
        return uploader;
    }

    public void setUploader(UserDto uploader) {
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

    public double getCamLatitude() {
        return camLatitude;
    }

    public void setCamLatitude(double camLatitude) {
        this.camLatitude = camLatitude;
    }

    public double getCamLongitude() {
        return camLongitude;
    }

    public void setCamLongitude(double camLongitude) {
        this.camLongitude = camLongitude;
    }

    public double getCamAzimuth() {
        return camAzimuth;
    }

    public void setCamAzimuth(double camAzimuth) {
        this.camAzimuth = camAzimuth;
    }

    public double getPosAccuracy() {
        return posAccuracy;
    }

    public void setPosAccuracy(double posAccuracy) {
        this.posAccuracy = posAccuracy;
    }

    public double getCamFOV() {
        return camFOV;
    }

    public void setCamFOV(double camFOV) {
        this.camFOV = camFOV;
    }

    public LocalDateTime getDatetimeTaken() {
        return datetimeTaken;
    }

    public void setDatetimeTaken(LocalDateTime datetimeTaken) {
        this.datetimeTaken = datetimeTaken;
    }

    public String getCamModel() {
        return camModel;
    }

    public void setCamModel(String camModel) {
        this.camModel = camModel;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getIso() {
        return iso;
    }

    public void setIso(int iso) {
        this.iso = iso;
    }

    public boolean isFlash() {
        return flash;
    }

    public void setFlash(boolean flash) {
        this.flash = flash;
    }

    public double getExpTime() {
        return expTime;
    }

    public void setExpTime(double expTime) {
        this.expTime = expTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhotoDto)) return false;
        PhotoDto photoDto = (PhotoDto) o;
        return Objects.equals(base64Id, photoDto.base64Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base64Id);
    }

    @Override
    public String toString() {
        return "PhotoDto{" +
                "id=" + id +
                ", uploader=" + uploader +
                ", uploadTime=" + uploadTime +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imageHash=" + Objects.toString(base64Id) +
                ", camLatitude=" + camLatitude +
                ", camLongitude=" + camLongitude +
                ", camAzimuth=" + camAzimuth +
                ", posAccuracy=" + posAccuracy +
                ", camFOV=" + camFOV +
                ", datetimeTaken=" + datetimeTaken +
                ", camModel='" + camModel + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", iso=" + iso +
                ", flash=" + flash +
                ", expTime=" + expTime +
                '}';
    }


}
