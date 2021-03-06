package com.miw.gildedrose.config;

import com.miw.gildedrose.auth.SecurityContextService;
import com.miw.gildedrose.filter.GrAuthHeaderFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) // to allow @PreAuthorize on methods
@EnableWebSecurity
public class GildedRoseSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private SecurityContextService securityContextService;

    @Bean
    GrAuthHeaderFilter authHeaderFilter() throws Exception {
        GrAuthHeaderFilter filter = new GrAuthHeaderFilter("/items/buy", securityContextService);
        filter.setAuthenticationManager(authenticationManager());

        return filter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable() // no basic authentication
                .csrf().disable() //no csrf token
                .formLogin().disable() // no form login
                .logout().disable() // we have our own logout
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and() // no session
                .authenticationProvider(authenticationProvider)
                // add auth token header filter before anonymous auth filter
                .addFilterBefore(authHeaderFilter(), AnonymousAuthenticationFilter.class)
                .authorizeHttpRequests()
                .antMatchers("/items/buy", "/account/create").authenticated() // authenticated request matchers
                .antMatchers("**/**").permitAll() // permit all others
                ;

    }

}
