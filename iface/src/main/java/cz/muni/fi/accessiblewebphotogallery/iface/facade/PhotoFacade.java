package cz.muni.fi.accessiblewebphotogallery.iface.facade;

import cz.muni.fi.accessiblewebphotogallery.iface.dto.AlbumDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.BuildingInfoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.PhotoDto;
import cz.muni.fi.accessiblewebphotogallery.iface.dto.UserDto;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PhotoFacade {

    PageImpl<PhotoDto> findByUploadTimeBetween(Instant begin, Instant end, Pageable pageable);

    PageImpl<PhotoDto> findByUploader(UserDto uploader, Pageable pageable);

    PageImpl<PhotoDto> findByDescriptionContaining(String partialDescription, Pageable pageable);

    PageImpl<PhotoDto> findByTitleContaining(String partialTitle, Pageable pageable);

    Optional<PhotoDto> findByBase64Id(String base64Id);

    PageImpl<PhotoDto> findAllMostRecentFirst(Pageable pageable);

    PageImpl<PhotoDto> findAllInAlbum(AlbumDto album, Pageable pageable);

    PageImpl<PhotoDto> findByBuildingInfoList(List<BuildingInfoDto> buildingInfoDtoList, Pageable pageable);

}
