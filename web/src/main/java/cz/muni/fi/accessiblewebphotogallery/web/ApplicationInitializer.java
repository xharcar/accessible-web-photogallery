package cz.muni.fi.accessiblewebphotogallery.web;

import cz.muni.fi.accessiblewebphotogallery.web.config.DispatcherConfig;
import cz.muni.fi.accessiblewebphotogallery.web.config.WebConfig;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class ApplicationInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext container) throws ServletException {
        AnnotationConfigWebApplicationContext rootCtx = new AnnotationConfigWebApplicationContext();
        rootCtx.register(WebConfig.class);

        container.addListener(new ContextLoaderListener(rootCtx));

        AnnotationConfigWebApplicationContext dispatcherCtx = new AnnotationConfigWebApplicationContext();
        dispatcherCtx.register(DispatcherConfig.class);

        ServletRegistration.Dynamic dispatcher = container.addServlet("dispatcher",new DispatcherServlet(dispatcherCtx));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}