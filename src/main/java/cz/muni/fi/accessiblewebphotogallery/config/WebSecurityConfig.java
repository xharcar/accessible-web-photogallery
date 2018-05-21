package cz.muni.fi.accessiblewebphotogallery.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("Configuring security");
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/user").permitAll()
                .antMatchers("/detail").permitAll()
                .antMatchers("/browse").permitAll()
                .antMatchers("/registration").anonymous()
                .antMatchers("/upload").authenticated()
                .antMatchers("/profile").authenticated()
                .antMatchers("/admin").hasRole("ADMINISTRATOR")
                .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                .and()
                .logout()
                    .permitAll();
    }

}
