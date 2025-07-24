package com.MindSpaceTeam.MindSpace.Config;

import com.MindSpaceTeam.MindSpace.Filters.LoginAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, LoginAuthenticationFilter loginAuthenticationFilter) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authorize) ->
                    authorize
                            .requestMatchers("/workspaces/**", "/workspace/**").authenticated()
                            .requestMatchers("/**").permitAll()
                )
                .addFilterBefore(loginAuthenticationFilter, BasicAuthenticationFilter.class)
                .build();
    }
}
