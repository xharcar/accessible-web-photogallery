package cz.muni.fi.accessiblewebphotogallery.web.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/login").anonymous()
                .antMatchers("/registration").anonymous()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/").permitAll()
                .antMatchers("/detail").permitAll()
                .antMatchers("/browse").permitAll()
                .and()
            .formLogin()
                .loginPage("/login").failureUrl("/login");
    }

}
