package cz.muni.fi.accessiblewebphotogallery.persistence.dao;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.BuildingInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildingInfoDao extends JpaRepository<BuildingInfo,Long> {

    // for retrieving building information pertaining to a given photo
    List<BuildingInfo> findByPhotoId(Long photoId);

    /* for looking up photos by building name;
        since there can be multiple buildings in a photo (see ERD), the idea is:
        1) look up BuildingInfo instances by building name
        2) retrieve photos to which found BuildingInfos are related by foreign key
        (pseudo-SQL: select * from photos where id in (select * from buildinginfo where buildingname = buildingName))
     */
    List<BuildingInfo> findByBuildingName(String buildingName);

}
