package com.systems.fele.users.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.systems.fele.users.security.RestAuthenticationEntryPoint;
import com.systems.fele.users.service.AppUserDetailsService;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    
    @Autowired
    public SecurityConfig(AppUserDetailsService appUserDetailsService, RestAuthenticationEntryPoint authenticationEntryPoint) {
        this.appUserDetailsService = appUserDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    private final AppUserDetailsService appUserDetailsService;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSec) throws Exception {
        httpSec.csrf(csrf -> csrf.disable())
                .httpBasic(configurer -> configurer.authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(this::configureAuthorizeHttpRequests);
        
        return httpSec.build();
    }
    
    private void configureAuthorizeHttpRequests(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        // TODO: Setup which endpoints should be open and for which roles
        authorize.requestMatchers(HttpMethod.POST, "/rest/api/users/register").authenticated()
            .requestMatchers("error").anonymous()
            .anyRequest().authenticated();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
            .authenticationProvider(authProvider())
            .build();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(appUserDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
}

    @Bean
    AppUserDetailsService userDetailsService() {
        return appUserDetailsService;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }



}
