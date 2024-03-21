package com.systems.fele.users.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.GenericFilterBean;

import com.systems.fele.users.security.RestAuthenticationEntryPoint;
import com.systems.fele.users.service.AppUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    
    @Autowired private AppUserDetailsService appUserDetailsService;
    
    @Autowired private RestAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSec) throws Exception {
        /*httpSec.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/rest/api/users/register").hasAuthority("ADMIN")
                .requestMatchers("/rest/api/users/").hasAuthority("ADMIN")
                .anyRequest().authenticated());
                //.requestMatchers("/rest/api/users").anonymous()
                //.anyRequest().authenticated());
        */
        httpSec.csrf().disable();

        httpSec.httpBasic((configurer) -> configurer
                .authenticationEntryPoint(authenticationEntryPoint)).authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(HttpMethod.POST, "/rest/api/users/register").authenticated()
                        .requestMatchers("error").anonymous()
                        .anyRequest().authenticated());


        var filter = new GenericFilterBean() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                chain.doFilter(request, response);
            }
        };

        //httpSec.addFilterAfter(filter, BasicAuthenticationFilter.class);

        return httpSec.build();
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
