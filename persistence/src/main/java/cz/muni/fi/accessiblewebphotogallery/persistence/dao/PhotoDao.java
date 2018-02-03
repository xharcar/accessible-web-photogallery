package cz.muni.fi.accessiblewebphotogallery.persistence.dao;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.PhotoEntity;
import cz.muni.fi.accessiblewebphotogallery.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;

public interface PhotoDao extends JpaRepository<PhotoEntity,Long> {

    /* Methods declared here are to be used to find photos by exact data, unless otherwise specified;
    Generally:
    Finds (one photo/all photos) by (given piece of data)
    @param datapiece data to look for
    @return Optional/List containing(if found) photo(s) with given datapiece
     */

    List<PhotoEntity> findByDateUploaded(Date dateUploaded);

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

    // find by camera model(eg. to help decide on a new camera) Y/n?

}
