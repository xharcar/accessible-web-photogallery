package cz.muni.fi.accessiblewebphotogallery.persistence.dao;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PhotoDao extends JpaRepository<PhotoEntity,Long> {

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
    List<PhotoEntity> findByUploadTimeBetween(Instant begin, Instant end);

    List<PhotoEntity> findByUploader(UserEntity uploader);

    /* Finds all photos whose description contains a given string;
        when searching by description on the website, the idea is:
        1) split the search string into tokens on spaces
        2) search for every token separately (this method)
        3) return the common elements(intersection) of the search result sets from 2)
     */
    List<PhotoEntity> findByDescriptionContaining(String searchString);

    // same as findByDescriptionContaining(), searches titles instead
    List<PhotoEntity> findByTitleContaining(String searchString);

    // check whether a base-64 id exists when saving new uploads
    Optional<PhotoEntity> findByBase64Identifier(String b64id);

    // sorted by upload time so most recent can be displayed first for browsing
    List<PhotoEntity> findAllByOrderByUploadTimeDesc();

}
