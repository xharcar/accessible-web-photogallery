package cz.muni.fi.accessiblewebphotogallery.persistence.entity;


import javax.persistence.*;

// Information about a building in a picture
@Entity
@Table(name = "BUILDINGINFO")
public class BuildingInfo {
// TBD: more OSM tags, Y/N (eg. RUIAN info)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "photo_id"))
    private Long photoId;

    @Column(nullable = true)
    private int distance;

    @Column(nullable = true)
    private String name;

    @Column(nullable = true, precision = 10)
    private double latitude;

    @Column(nullable = true, precision = 10)
    private double longitude;

}
