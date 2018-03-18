package cz.muni.fi.accessiblewebphotogallery.application.service.iface;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public interface PagedService<T> {

    PageImpl<T> findAll(Pageable pageable);

}
