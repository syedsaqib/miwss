package com.miw.gildedrose.config;

import com.miw.gildedrose.filter.GrAuthHeaderFilter;
import lombok.RequiredArgsConstructor;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) // to allow @PreAuthorize on methods
@EnableWebSecurity
@RequiredArgsConstructor
public class GildedRoseSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AuthenticationProvider authenticationProvider;

    @Bean
    GrAuthHeaderFilter authHeaderFilter() throws Exception {
        GrAuthHeaderFilter filter = new GrAuthHeaderFilter(getAuthenticatedUrlsMatcher());
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
            .requestMatchers(getAuthenticatedUrlsMatcher()).authenticated() // authenticated request matchers
            .antMatchers("**/**").permitAll() // permit all others
            ;

    }

    protected static RequestMatcher getAuthenticatedUrlsMatcher() {
        return new OrRequestMatcher(
                new AntPathRequestMatcher("/items/buy"),
                new AntPathRequestMatcher("/items/clicks/*"),
                new AntPathRequestMatcher("/account/logo*"), // will include /logout and /logoff both
                new AntPathRequestMatcher("/account/create")
        );
    }

}
