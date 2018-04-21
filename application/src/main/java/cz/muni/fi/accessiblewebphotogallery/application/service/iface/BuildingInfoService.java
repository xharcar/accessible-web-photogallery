package cz.muni.fi.accessiblewebphotogallery.application.service.iface;

import com.google.gson.JsonObject;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.BuildingInfo;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;

import java.util.List;
import java.util.Map;

public interface BuildingInfoService {

    List<BuildingInfo> findAll();

    List<BuildingInfo> findByPhoto(PhotoEntity photo);

    List<BuildingInfo> findByBuildingNameContainingIgnoreCase(String search);

    List<BuildingInfo> findByOsmId(Long osmId);

    List<BuildingInfo> findByLatitudeAndLongitude(double lat, double lon);

    List<BuildingInfo> saveBuildingInfos(Map<JsonObject,JsonObject> buildingMap, JsonObject camData, PhotoEntity photo);

    BuildingInfo updateBuildingInfo(BuildingInfo buildingInfo);

    void delete(BuildingInfo buildingInfo);

}
