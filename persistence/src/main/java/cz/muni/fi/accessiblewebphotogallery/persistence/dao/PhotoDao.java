package cz.muni.fi.accessiblewebphotogallery.persistence.dao;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PhotoDao extends JpaRepository<PhotoEntity, Long> {

    /* Methods declared here are to be used to find photos by exact data, unless otherwise specified;
    Generally:
    Finds (one photo/all photos) by (given piece of data)
    @param datapiece data to look for
    @return Optional/List containing(if found) photo(s) with given datapiece
     */

    // can be used for single-day searches with begin at 23:59:59.999 previous day, end at 00:00:00.001 the next day;
    // can test "after X" with "between X and now" and "before X" with "between epoch and X"
    // line of thinking: this doesn't lower the required number of DB accesses when searching,
    // and one more comparison or two is not a big deal (couple cycles)
    Page<PhotoEntity> findByUploadTimeBetween(Instant begin, Instant end, Pageable pageable);

    Page<PhotoEntity> findByUploader(UserEntity uploader, Pageable pageable);

    /* Finds all photos whose description contains a given string;
        when searching by description on the website, the idea is:
        1) split the search string into tokens on spaces
        2) search for every token separately (this method)
        3) return the common elements(intersection) of the search result sets from 2)
     */
    Page<PhotoEntity> findByDescriptionContainingIgnoreCase(String searchString, Pageable pageable);

    // same as findByDescriptionContainingIgnoreCase(), searches titles instead
    Page<PhotoEntity> findByTitleContainingIgnoreCase(String searchString, Pageable pageable);

    // check whether a base-64 id exists when saving new uploads
    Optional<PhotoEntity> findById(String b64id);

    // sorted by upload time so most recent can be displayed first for browsing
    Page<PhotoEntity> findAllByOrderByUploadTimeDesc(Pageable pageable);

}
