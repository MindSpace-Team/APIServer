package com.MindSpaceTeam.MindSpace.Filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class LoginAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Filter start");
        String path = request.getRequestURI();
        if (!(path.startsWith("/workspace") || path.startsWith("/workspaces"))) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            log.warn("Cookie is required");
            response.sendRedirect("/login");
        }

        String sid = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("sid")) {
                sid = cookie.getValue();
            }
        }

        if (sid == null) {
            log.warn("Key of sid is not exist");
            response.sendRedirect("/login");
        }

        if (!redisTemplate.hasKey("spring:session:sessions:" + sid)) {
            log.warn("Key of sid is not exist in redis");
            response.sendRedirect("/login");
        }

        log.info("Authentication successful");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(sid, null, List.of()));
        filterChain.doFilter(request, response);
    }
}
