package com.br.tickets.config;

import com.br.tickets.auth.services.JwtService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())      
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .httpBasic(Customizer.withDefaults())
            .formLogin(Customizer.withDefaults());

        return http.build();
    }
    
    @Bean
    public JwtService jwtService() {
        return Mockito.mock(JwtService.class);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return Mockito.mock(UserDetailsService.class);
    }
}
