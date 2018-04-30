package cz.muni.fi.accessiblewebphotogallery.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties
public class ApplicationConfig {

    @Value("${rootdir}")
    private String rootDBDirectorySetting;

    @Value("${photodir}")
    private String photoDirectorySetting;

    @Value("${albumdir}")
    private String albumDirectorySetting;

    public String getRootDBDirectory(){
        if(rootDBDirectorySetting.equalsIgnoreCase("default")
                || rootDBDirectorySetting.isEmpty()){
            return System.getProperty("user.home");
        }
        return rootDBDirectorySetting;
    }

    public String getPhotoDirectory(){
        if(photoDirectorySetting.equalsIgnoreCase("default")
                || photoDirectorySetting.isEmpty()){
            return getRootDBDirectory() + File.separator + "photos";
        }
        return photoDirectorySetting;
    }

    public String getAlbumDirectory(){
        if(albumDirectorySetting.equalsIgnoreCase("default")
                || albumDirectorySetting.isEmpty()){
            return getRootDBDirectory() + File.separator + "albums";
        }
        return albumDirectorySetting;
    }
}
