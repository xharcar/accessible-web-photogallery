package cz.muni.fi.accessiblewebphotogallery.application.service.iface;

import com.google.gson.JsonObject;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.BuildingInfo;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BuildingInfoService {

    /**
     * Retrieves all building records
     *
     * @return List containing all building records in the DB
     */
    List<BuildingInfo> findAll();

    /**
     * Retrieves a building by its DB ID
     * @param id B ID we're looking for
     * @return empty if not found, else Optional with BuildingInfo with the correct ID
     */
    Optional<BuildingInfo> findById(String id);

    /**
     * Retrieves building records by photo
     *
     * @param photo Photo to search
     * @return List of BuildingInfo instances belonging to photo
     */
    List<BuildingInfo> findByPhoto(PhotoEntity photo);

    /**
     * Retrieves building records by building name, ignoring case
     *
     * @param searchStr (partial) building name to search
     * @return List of BuildingInfo instances whose buildingNames match searchStr
     */
    List<BuildingInfo> findByBuildingNameApx(String searchStr);

    /**
     * Retrieves buildings by OSM ID
     *
     * @param osmId OSM ID to search
     * @return List of BuildingInfo instances whose OSM ID == osmId
     */
    List<BuildingInfo> findByOsmId(Long osmId);

    /**
     * Retrieves buildings at given coordinates or close enough to be certain it's the same building
     *
     * @param lat Latitude to search
     * @param lon Longitude to search
     * @return List of BuildingInfo instances within (implementation-defined) limits close to {lat;lon}
     */
    List<BuildingInfo> findByGPSPosition(double lat, double lon);

    /**
     * Registers buildings in a photo
     *
     * @param buildingMap Map containing JsonObjects retrieved from the supplementary JSON file as keys, and their
     *                    equivalents retrieved from Nominatim (or other source) as values
     * @param camData     JsonObject containing information about the position of the camera, retrieved from the
     *                    supplementary JSON file
     * @param photo       PhotoEntity to which the building entries belong
     * @return List of BuildingInfo instances as saved, with all the available data written
     */
    List<BuildingInfo> registerBuildings(Map<JsonObject, JsonObject> buildingMap, JsonObject camData, PhotoEntity photo);

    /**
     * Updates a BuildingInfo instance, eg. when a user wants to edit its focus text
     *
     * @param buildingInfo BuildinghInfo to update
     * @return BuildingInfo as saved
     */
    BuildingInfo updateBuildingInfo(BuildingInfo buildingInfo);


    List<BuildingInfo> updateBuildings(List<BuildingInfo> buildingList);

    /**
     * Deletes a building entry from the DB
     *
     * @param buildingInfo building entry to delete
     */
    void delete(BuildingInfo buildingInfo);

}
