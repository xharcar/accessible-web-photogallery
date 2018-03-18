package cz.muni.fi.accessiblewebphotogallery.persistence.dao;

import cz.muni.fi.accessiblewebphotogallery.persistence.entity.RegistrationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistrationTokenDao extends JpaRepository<RegistrationToken,Long>{

    Optional<RegistrationToken> findByEmail(String email);

}
