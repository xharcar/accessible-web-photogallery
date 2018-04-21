package cz.muni.fi.accessiblewebphotogallery.application.facade;

import com.google.gson.JsonObject;
import cz.muni.fi.accessiblewebphotogallery.application.service.iface.BuildingInfoService;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.BuildingInfoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.facade.BuildingInfoFacade;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.BuildingInfo;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import org.dozer.Mapper;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BuildingInfoFacadeImpl implements BuildingInfoFacade {

    private Mapper mapper;
    private BuildingInfoService infoService;

    @Inject
    public BuildingInfoFacadeImpl(Mapper mapper, BuildingInfoService infoService) {
        this.mapper = mapper;
        this.infoService = infoService;
    }

    @Override
    public List<BuildingInfoDto> findByPhoto(PhotoDto photo) {
        return infoService.findByPhoto(photoDtoToEntity(photo)).stream().map(this::buildingInfoToDto).collect(Collectors.toList());
    }

    @Override
    public List<BuildingInfoDto> findByBuildingNameContainingIgnoreCase(String buildingName) {
        return infoService.findByBuildingNameContainingIgnoreCase(buildingName).stream().map(this::buildingInfoToDto).collect(Collectors.toList());
    }

    @Override
    public List<BuildingInfoDto> findByOsmId(Long osmId) {
        return infoService.findByOsmId(osmId).stream().map(this::buildingInfoToDto).collect(Collectors.toList());
    }

    @Override
    public List<BuildingInfoDto> findByGPS(double lat, double lon) {
        return infoService.findByLatitudeAndLongitude(lat,lon).stream().map(this::buildingInfoToDto).collect(Collectors.toList());
    }

    @Override
    public List<BuildingInfoDto> registerBuildingsFromJson(Map<JsonObject, JsonObject> jsonMap, JsonObject camPosition, PhotoDto photoDto) {
        return infoService.saveBuildingInfos(jsonMap,camPosition, photoDtoToEntity(photoDto)).stream().map(this::buildingInfoToDto).collect(Collectors.toList());
    }

    @Override
    public BuildingInfoDto updateBuildingInfo(BuildingInfoDto buildingDto) {
        return buildingInfoToDto(infoService.updateBuildingInfo(buildingDtoToEntity(buildingDto)));
    }

    @Override
    public void delete(BuildingInfoDto buildingDto) {
        infoService.delete(buildingDtoToEntity(buildingDto));
    }

    private PhotoEntity photoDtoToEntity(PhotoDto dto){return mapper.map(dto,PhotoEntity.class);}
    private BuildingInfoDto buildingInfoToDto(BuildingInfo info){return mapper.map(info,BuildingInfoDto.class);}
    private BuildingInfo buildingDtoToEntity(BuildingInfoDto dto){return mapper.map(dto,BuildingInfo.class);}
}
