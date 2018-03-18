package cz.muni.fi.accessiblewebphotogallery.iface.facade;

import cz.muni.fi.accessiblewebphotogallery.iface.dto.BuildingInfoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.PhotoDto;

import java.util.List;

public interface BuildingInfoFacade {

    List<BuildingInfoDto> findByPhoto(PhotoDto photo);

    List<BuildingInfoDto> findByBuildingNameContainingIgnoreCase(String buildingName);

}
