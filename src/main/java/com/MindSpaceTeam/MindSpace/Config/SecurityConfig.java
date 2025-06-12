package com.MindSpaceTeam.MindSpace.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain sercurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable) // 나중에 서비스 배포 시 diable삭제해야함
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(http ->
                            http.requestMatchers("/workspaces/**", "/workspace/**").authenticated()
                                    .anyRequest().permitAll()
                ).build();
    }
}
