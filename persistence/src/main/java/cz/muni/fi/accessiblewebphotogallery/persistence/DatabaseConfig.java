package cz.muni.fi.accessiblewebphotogallery.persistence;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.File;
import java.util.Properties;

@Component
@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties
@EnableTransactionManagement
@EnableJpaRepositories
public class DatabaseConfig {

    @Inject
    private Environment env;

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

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(){
        LocalContainerEntityManagerFactoryBean rv = new LocalContainerEntityManagerFactoryBean();
        rv.setDataSource(dataSource());
        rv.setPackagesToScan(env.getProperty("packages-to-scan"));
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        rv.setJpaVendorAdapter(vendorAdapter);
        rv.setJpaProperties(additionalProperties());
        return rv;
    }

    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
        return new PersistenceExceptionTranslationPostProcessor();
    }

    private Properties additionalProperties(){
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto",env.getProperty("hibernate.hbm2ddl.auto"));
        properties.setProperty("hibernate.dialect",env.getProperty("hibernate.dialect"));
        return properties;
    }

}
