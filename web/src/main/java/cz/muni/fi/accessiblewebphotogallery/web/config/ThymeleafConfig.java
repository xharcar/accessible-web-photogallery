package cz.muni.fi.accessiblewebphotogallery.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class ThymeleafConfig implements WebMvcConfigurer {

    @Bean
    public SpringResourceTemplateResolver thymeleafTemplateResolver(){
        SpringResourceTemplateResolver rv = new SpringResourceTemplateResolver();
        rv.setPrefix("/WEB-INF/html");
        rv.setSuffix(".html");
        rv.setTemplateMode("HTML5");
        rv.setCharacterEncoding("UTF-8");
        return rv;
    }

    @Bean
    public SpringTemplateEngine templateEngine(){
        SpringTemplateEngine rv = new SpringTemplateEngine();
        rv.setTemplateResolver(thymeleafTemplateResolver());
        Set<IDialect> dialects = new HashSet<>();
        dialects.add(new SpringSecurityDialect());
        rv.setAdditionalDialects(dialects);
        return rv;
    }

    @Bean
    public ThymeleafViewResolver thymeleafViewResolver(){
        ThymeleafViewResolver rv = new ThymeleafViewResolver();
        rv.setTemplateEngine(templateEngine());
        rv.setCharacterEncoding("UTF-8");
        rv.setOrder(2);
        return rv;
    }


}
