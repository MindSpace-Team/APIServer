package com.MindSpaceTeam.MindSpace.Config;

import com.MindSpaceTeam.MindSpace.Filters.LoginAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Value("${mindspace.jwt.secret-key}")
    private String secretKey;

    @Bean
    public SecurityFilterChain sercurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        LoginAuthenticationFilter loginAuthenticationFilter = new LoginAuthenticationFilter();
        loginAuthenticationFilter.setSecretKey(secretKey);

        return httpSecurity
                .authorizeHttpRequests((authorize) ->
                    authorize
                            .requestMatchers("/workspaces/**", "/workspace/**").authenticated()
                            .requestMatchers("/**").permitAll()
                )
                .addFilterBefore(loginAuthenticationFilter, BasicAuthenticationFilter.class)
                .build();
    }
}
