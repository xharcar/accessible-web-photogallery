package cz.muni.fi.accessiblewebphotogallery.persistence.entity;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
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
    private Instant uploadTime;

    @Column(nullable = false, length = 128) // 128 characters should be enough for a title (YouTube has 100)
    private String title;

    @Column(nullable = false, length = 2048) // 2048 characters should be plenty for a nice description
    private String description;

    @Column(nullable = false, length = 12, unique = true)
    private String base64Id;
    /* Identifier Mk.3
        1) Register upload Instant
        2) Into a List of ByteBuffers, put:
            - uploading user's DTO hashCode() result
            - hashCode() of upload Instant
        3) Generate 4 pseudo-random bytes, append to ByteBuffer list
        4) Update a MessageDigest(MD5) with the ByteBuffer arrays
        5) Update the digest with the photo
        6) If a metadata file is present, update the digest with it
        7) Finalise the digest
        8) Take the 9 least significant bytes and convert them to Base64(URL-safe)
        9) If the resulting String is already present, goto 3; else assign it as identifier
     */

    // camera info - nullable for when no JSON metadata file is uploaded
    @Column(nullable = true, precision = 10)
    private Double cameraLatitude;

    @Column(nullable = true, precision = 10)
    private Double cameraLongitude;

    @Column(nullable = true, precision = 1)
    private Double cameraAzimuth;

    @Column(nullable = true, precision = 1)
    private Double positionAccuracy;

    @Column(nullable = true, precision = 2)
    private Double cameraFOV;

    // Some basic EXIF metadata- all non-mandatory- will be considered null for storage purposes if not present
    @Column(nullable = true)
    private LocalDateTime datetimeTaken; // EXIF has up to minute precision

    @Column(nullable = true, length = 96)
    private String cameraModel;

    @Column(nullable = true)
    private Integer imageWidth;

    @Column(nullable = true)
    private Integer imageHeight;

    @Column(nullable = true)
    private Integer iso;

    @Column(nullable = true)
    private Boolean flash;

    @Column(nullable = true)
    private Double exposureTime;
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

    public void setBase64Id(String base64Identifier) {
        this.base64Id = base64Identifier;
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

    public void setCameraFOV(Double cameraHorizontalFOV) {
        this.cameraFOV = cameraHorizontalFOV;
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
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof PhotoEntity)) return false;
        PhotoEntity that = (PhotoEntity) o;
        return Objects.equals(base64Id, that.base64Id);
        // no other element needed due to aforementioned b64ID generation strategy
    }

    @Override
    public int hashCode() {
        return Objects.hash(base64Id);
    }

    @Override
    public String toString() {
        return "PhotoEntity{" +
                "id=" + id +
                ", uploader=" + uploader +
                ", uploadTime=" + uploadTime +
                ", imageHash=" + Objects.toString(base64Id) +
                ", cameraLatitude=" + cameraLatitude +
                ", cameraLongitude=" + cameraLongitude +
                ", cameraAzimuth=" + cameraAzimuth +
                ", positionAccuracy=" + positionAccuracy +
                ", cameraHorizontalFOV=" + cameraFOV +
                ", datetimeTaken=" + datetimeTaken +
                ", cameraModel='" + cameraModel +
                ", imageWidth=" + imageWidth +
                ", imageHeight=" + imageHeight +
                ", iso=" + iso +
                ", flash=" + flash +
                ", exposureTime=" + exposureTime +
                '}';
    }
}
