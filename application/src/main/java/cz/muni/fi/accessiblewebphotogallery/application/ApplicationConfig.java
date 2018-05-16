package cz.muni.fi.accessiblewebphotogallery.application;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Configuration
@ComponentScan(basePackages = {"cz.muni.fi.accessiblewebphotogallery.application.service","cz.muni.fi.accessiblewebphotogallery.persistence"})
@PropertySource("/application.properties")
@ConfigurationProperties
public class ApplicationConfig {
/*
File placement- the idea is for this to be able to change depending on OS and user preference
default is (user home directory)/photogallery as {root}, {root}/photos for photos and metadata files
(one directory per photo with dir name == photo base64id (see PhotoEntity.java)),
{root}/albums/(album Base-64 ID).txt for albums;
if empty, assumed default
*/
    private String rootDBDirectorySetting = "default";
    private String photoDirectorySetting = "default";
    private String albumDirectorySetting = "default";

    public String getRootDBDirectory() {
        if (rootDBDirectorySetting.equalsIgnoreCase("default")
                || rootDBDirectorySetting.isEmpty()) {
            return System.getProperty("user.home");
        }
        return rootDBDirectorySetting;
    }

    public String getPhotoDirectory() {
        if (photoDirectorySetting.equalsIgnoreCase("default")
                || photoDirectorySetting.isEmpty()) {
            return getRootDBDirectory() + File.separator + "photos";
        }
        return photoDirectorySetting;
    }

    public String getAlbumDirectory() {
        if (albumDirectorySetting.equalsIgnoreCase("default")
                || albumDirectorySetting.isEmpty()) {
            return getRootDBDirectory() + File.separator + "albums";
        }
        return albumDirectorySetting;
    }
}
