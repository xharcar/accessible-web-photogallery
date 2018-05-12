package cz.muni.fi.accessiblewebphotogallery.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class DispatcherConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/html/**").addResourceLocations("/html/");
        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
    }
}
