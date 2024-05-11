package com.systems.fele.tests.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;

import com.systems.fele.users.repository.AppUserRepository;

@TestConfiguration
public class IntegrationTestConfig {
    @Autowired
    AppUserRepository appUserRepository;
    /*@Bean
    @Primary
    public UserDetailsService userDetailsServiceTest() {
        var users = new ArrayList<UserDetails>();
        users.add(new AppUserPrincipal(
            new AppUser(1000l, "Toshino", "Kyouko", "toshino.kyouko@weebmail.com", "aaaa", false, false)
        ));

        return new InMemoryUserDetailsManager(users);
    }

    @Bean
    public DaoAuthenticationProvider authProviderTest(@Autowired UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }*/
}
