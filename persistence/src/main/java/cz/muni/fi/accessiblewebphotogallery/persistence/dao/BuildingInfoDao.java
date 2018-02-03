package cz.muni.fi.accessiblewebphotogallery.persistence.dao;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.BuildingInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildingInfoDao extends JpaRepository<BuildingInfo,Long> {

    // for retrieving building information pertaining to a given photo
    List<BuildingInfo> findByPhotoId(Long photoId);

}
