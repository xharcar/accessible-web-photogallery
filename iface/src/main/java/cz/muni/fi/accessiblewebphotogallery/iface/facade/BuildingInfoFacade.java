package cz.muni.fi.accessiblewebphotogallery.iface.facade;

import com.google.gson.JsonObject;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.BuildingInfoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.PhotoDto;

import java.util.List;
import java.util.Map;

public interface BuildingInfoFacade {

    List<BuildingInfoDto> findByPhoto(PhotoDto photo);

    List<BuildingInfoDto> findByBuildingNameContainingIgnoreCase(String buildingName);

    List<BuildingInfoDto> findByOsmId(Long osmId);

    List<BuildingInfoDto> findByGPS(double lat, double lon);

    // Takes a Map<JsonObject k,JsonObject v>;
    // where k = JsonObject from file;
    // v = JsonObject from Nominatim/elsewhere corresponding to k
    // iterates over k's, creates a BuildingInfo from <k,v>, returns a BuildingInfo on each, puts them all into ret val
    List<BuildingInfoDto> registerBuildingsFromJson(Map<JsonObject,JsonObject> jsonMap, JsonObject camPosition, PhotoDto photoDto);

    BuildingInfoDto updateBuildingInfo(BuildingInfoDto buildingDto);

    public void delete(BuildingInfoDto building);


}
