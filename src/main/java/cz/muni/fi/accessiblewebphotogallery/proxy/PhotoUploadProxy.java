package cz.muni.fi.accessiblewebphotogallery.proxy;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Simple stupid class that takes user input upon uploading a photo; for now, since editing isn't allowed, this will do
 */
public class PhotoUploadProxy {

    @NotBlank
    @Size(min = 1, max = 128)
    @Pattern(regexp = "[\\p{Print}]+")
    private String title;

    @NotNull
    @Size(max = 2048)
    @Pattern(regexp = "[\\p{Print}]+")
    private String description;

}
