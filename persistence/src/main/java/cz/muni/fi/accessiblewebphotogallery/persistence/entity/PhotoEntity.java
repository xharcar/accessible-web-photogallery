package cz.muni.fi.accessiblewebphotogallery.persistence.entity;

import javax.persistence.*;
import java.sql.Date;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "PHOTOS")
public class PhotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity uploader;

    @Column(nullable = false)
    private Date dateUploaded;

    @Column(nullable = false, length = 128) // 128 characters should be enough for a title (YouTube has 100)
    private String title;

    @Column(nullable = false, length = 2048) // 2048 characters should be plenty for a nice description
    private String description;

    @Column(nullable = false)
    private byte[] imageHash; // SHA1(/MD5) of the photo for identification purposes, similar to GitHub commit hashes

    @Column(nullable = false, precision = 10)
    private double cameraLatitude;

    @Column(nullable = false, precision = 10)
    private double cameraLongitude;

    @Column(nullable = false, precision = 1)
    private double cameraAzimuth;

    @Column(nullable = false, precision = 1)
    private double positionAccuracy;

    @Column(nullable = false, precision = 2)
    private double cameraHorizontalFOV;

    // Some basic EXIF metadata- all non-mandatory- will be considered null for storage purposes if not present
    @Column(nullable = true)
    private Date dateTaken;

    @Column(nullable = true)
    private String cameraModel;

    @Column(nullable = true)
    private int imageWidth;

    @Column(nullable = true)
    private int imageHeight;

    @Column(nullable = true)
    private int iso;

    @Column(nullable = true)
    private boolean flash;

    @Column(nullable = true)
    private double exposureTime;
    // </EXIF>

    public PhotoEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUploader() {
        return uploader;
    }

    public void setUploader(UserEntity uploader) {
        this.uploader = uploader;
    }

    public Date getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(Date dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public byte[] getImageHash() {
        return imageHash;
    }

    public void setImageHash(byte[] imageHash) {
        this.imageHash = imageHash;
    }

    public double getCameraLatitude() {
        return cameraLatitude;
    }

    public void setCameraLatitude(double cameraLatitude) {
        this.cameraLatitude = cameraLatitude;
    }

    public double getCameraLongitude() {
        return cameraLongitude;
    }

    public void setCameraLongitude(double cameraLongitude) {
        this.cameraLongitude = cameraLongitude;
    }

    public double getCameraAzimuth() {
        return cameraAzimuth;
    }

    public void setCameraAzimuth(double cameraAzimuth) {
        this.cameraAzimuth = cameraAzimuth;
    }

    public double getPositionAccuracy() {
        return positionAccuracy;
    }

    public void setPositionAccuracy(double positionAccuracy) {
        this.positionAccuracy = positionAccuracy;
    }

    public double getCameraHorizontalFOV() {
        return cameraHorizontalFOV;
    }

    public void setCameraHorizontalFOV(double cameraHorizontalFOV) {
        this.cameraHorizontalFOV = cameraHorizontalFOV;
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getCameraModel() {
        return cameraModel;
    }

    public void setCameraModel(String cameraModel) {
        this.cameraModel = cameraModel;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
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

    public double getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(double exposureTime) {
        this.exposureTime = exposureTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof PhotoEntity)) return false;
        PhotoEntity that = (PhotoEntity) o;
        return Objects.equals(uploader, that.uploader) &&
                Objects.equals(dateUploaded, that.dateUploaded) &&
                Arrays.equals(imageHash, that.imageHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uploader, dateUploaded, imageHash);
    }

    @Override
    public String toString() {
        return "PhotoEntity{" +
                "id=" + id +
                ", uploader=" + uploader +
                ", dateUploaded=" + dateUploaded +
                ", imageHash=" + Arrays.toString(imageHash) +
                ", cameraLatitude=" + cameraLatitude +
                ", cameraLongitude=" + cameraLongitude +
                ", cameraAzimuth=" + cameraAzimuth +
                ", positionAccuracy=" + positionAccuracy +
                ", cameraHorizontalFOV=" + cameraHorizontalFOV +
                ", dateTaken=" + dateTaken +
                ", cameraModel='" + cameraModel + '\'' +
                ", imageWidth=" + imageWidth +
                ", imageHeight=" + imageHeight +
                ", iso=" + iso +
                ", flash=" + flash +
                ", exposureTime=" + exposureTime +
                '}';
    }
}
