package cz.muni.fi.accessiblewebphotogallery.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "cz.muni.fi.accessiblewebphotogallery.config")
public class WebConfig {

}
