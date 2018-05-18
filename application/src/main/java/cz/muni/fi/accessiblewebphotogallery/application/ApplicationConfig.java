package cz.muni.fi.accessiblewebphotogallery.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Configuration
@EnableConfigurationProperties
@ComponentScan(basePackages = {"cz.muni.fi.accessiblewebphotogallery.application.service","cz.muni.fi.accessiblewebphotogallery.persistence"})
@PropertySource("/application.properties")
@ConfigurationProperties
public class ApplicationConfig {

    @Autowired
    private Environment environment;

    public String getRootDBDirectory() {
        String rootDBDirectorySetting = environment.getProperty("rootdir");
        if (rootDBDirectorySetting == null || rootDBDirectorySetting.isEmpty()
                || rootDBDirectorySetting.equalsIgnoreCase("default")) {
            return System.getProperty("user.home");
        }
        return rootDBDirectorySetting;
    }

    public String getPhotoDirectory() {
        String photoDirectorySetting = environment.getProperty("photodir");
        if (photoDirectorySetting == null || photoDirectorySetting.isEmpty()
                || photoDirectorySetting.equalsIgnoreCase("default")) {
            return getRootDBDirectory() + File.separator + "photos";
        }
        return photoDirectorySetting;
    }

    public String getAlbumDirectory() {
        String albumDirectorySetting = environment.getProperty("albumdir");
        if (albumDirectorySetting == null || albumDirectorySetting.isEmpty()
                || albumDirectorySetting.equalsIgnoreCase("default")) {
            return getRootDBDirectory() + File.separator + "albums";
        }
        return albumDirectorySetting;
    }
}
