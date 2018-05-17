package cz.muni.fi.accessiblewebphotogallery.persistence;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Objects;
import java.util.Properties;

@Component
@Configuration
@EnableConfigurationProperties
@PropertySource("classpath:/persistence.properties")
@EnableJpaRepositories
@EnableTransactionManagement
public class DatabaseConfig {

    @Autowired
    private Environment environment;


    private final String dialect = "org.hibernate.dialect.PostgreSQLDialect";
    private final String hbm2ddlAuto = "create-drop";


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean rv = new LocalContainerEntityManagerFactoryBean();
        rv.setDataSource(dataSource());
        rv.setPackagesToScan(Objects.requireNonNull(environment.getProperty("packages-to-scan")));
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        rv.setJpaVendorAdapter(vendorAdapter);
        rv.setJpaProperties(additionalProperties());
        return rv;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(environment.getProperty("spring.datasource.driver-class-name")));
        dataSource.setUrl(Objects.requireNonNull(environment.getProperty("spring.datasource.url")));
        dataSource.setUsername(Objects.requireNonNull(environment.getProperty("spring.datasource.username")));
        dataSource.setPassword(Objects.requireNonNull(environment.getProperty("spring.datasource.password")));
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", Objects.requireNonNull(environment.getProperty("hibernate.hbm2ddl.auto")));
        properties.setProperty("hibernate.dialect", Objects.requireNonNull(environment.getProperty("hibernate.dialect")));
        return properties;
    }


}
