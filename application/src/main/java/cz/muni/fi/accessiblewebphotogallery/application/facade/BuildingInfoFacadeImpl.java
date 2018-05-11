package cz.muni.fi.accessiblewebphotogallery.application.facade;

import com.google.gson.JsonObject;
import cz.muni.fi.accessiblewebphotogallery.application.PhotoGalleryBackendMapper;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.BuildingInfoService;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.BuildingInfoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.BuildingInfoFacade;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.BuildingInfo;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BuildingInfoFacadeImpl implements BuildingInfoFacade {

    private BuildingInfoService infoService;

    @Inject
    public BuildingInfoFacadeImpl(BuildingInfoService infoService) {
        this.infoService = infoService;
    }

    @Override
    public List<BuildingInfoDto> findAll() {
        return infoService.findAll().stream().map(this::buildingInfoToDto).collect(Collectors.toList());
    }

    @Override
    public List<BuildingInfoDto> findByPhoto(PhotoDto photo) {
        return infoService.findByPhoto(photoDtoToEntity(photo)).stream().map(this::buildingInfoToDto).collect(Collectors.toList());
    }

    @Override
    public List<BuildingInfoDto> findByBuildingNameApx(String buildingName) {
        return infoService.findByBuildingNameApx(buildingName).stream().map(this::buildingInfoToDto).collect(Collectors.toList());
    }

    @Override
    public List<BuildingInfoDto> findByOsmId(Long osmId) {
        return infoService.findByOsmId(osmId).stream().map(this::buildingInfoToDto).collect(Collectors.toList());
    }

    @Override
    public List<BuildingInfoDto> findByGPSPosition(double lat, double lon) {
        return infoService.findByGPSPosition(lat,lon).stream().map(this::buildingInfoToDto).collect(Collectors.toList());
    }

    @Override
    public List<BuildingInfoDto> registerBuildings(Map<JsonObject, JsonObject> jsonMap, JsonObject camPosition, PhotoDto photoDto) {
        return infoService.registerBuildings(jsonMap,camPosition, photoDtoToEntity(photoDto)).stream().map(this::buildingInfoToDto).collect(Collectors.toList());
    }

    @Override
    public BuildingInfoDto updateBuildingInfo(BuildingInfoDto buildingDto) {
        return buildingInfoToDto(infoService.updateBuildingInfo(buildingDtoToEntity(buildingDto)));
    }

    @Override
    public List<BuildingInfoDto> updateBuildings(List<BuildingInfoDto> buildingList) {
        return infoService.updateBuildings(buildingList.stream().map(this::buildingDtoToEntity).collect(Collectors.toList())).stream().map(this::buildingInfoToDto).collect(Collectors.toList());
    }

    @Override
    public void delete(BuildingInfoDto buildingDto) {
        infoService.delete(buildingDtoToEntity(buildingDto));
    }

    private PhotoEntity photoDtoToEntity(PhotoDto dto){return PhotoGalleryBackendMapper.photoDtoToEntity(dto);}
    private BuildingInfoDto buildingInfoToDto(BuildingInfo info){return PhotoGalleryBackendMapper.buildingInfoToDto(info);}
    private BuildingInfo buildingDtoToEntity(BuildingInfoDto dto){return PhotoGalleryBackendMapper.buildingInfoDtoToEntity(dto);}
}
