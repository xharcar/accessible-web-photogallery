package cz.muni.fi.accessiblewebphotogallery.persistence;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import java.io.File;

@Component
@PropertySource("classpath:persistence.properties")
@ConfigurationProperties
public class DatabaseProperties {

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
        else return rootDBDirectorySetting;
    }

    public String getPhotoDirectory(){
        if(photoDirectorySetting.equalsIgnoreCase("default")
                || photoDirectorySetting.isEmpty()){
            return getRootDBDirectory() + File.separator + "photos";
        }
        else return photoDirectorySetting;
    }

    public String getAlbumDirectory(){
        if(albumDirectorySetting.equalsIgnoreCase("default")
                || albumDirectorySetting.isEmpty()){
            return getRootDBDirectory() + File.separator + "albums";
        }
        else return albumDirectorySetting;
    }

}
