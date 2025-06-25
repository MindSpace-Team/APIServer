package com.MindSpaceTeam.MindSpace.Service;

import com.MindSpaceTeam.MindSpace.Components.Auth.Token.Exception.RefreshTokenExpiredException;
import com.MindSpaceTeam.MindSpace.Components.Auth.Token.JwtTokenizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
public class RefreshService {

    private RedisTemplate<String, String> redisTemplate;
    private JwtTokenizer jwtTokenizer;

    public RefreshService(RedisTemplate<String, String> redisTemplate, JwtTokenizer jwtTokenizer) {
        this.redisTemplate = redisTemplate;
        this.jwtTokenizer = jwtTokenizer;
    }

    public String refreshAccessToken(String refreshToken) throws RefreshTokenExpiredException {
        String redisKey = "refresh: %s".formatted(refreshToken);

        if (!this.redisTemplate.hasKey(redisKey)) {
            log.warn("User's refresh token was expired");
            throw new RefreshTokenExpiredException("Refresh Token was expired pleas login again");
        }

        String userId = Objects.requireNonNull(this.redisTemplate.opsForHash().get(redisKey, "userID")).toString();

        return jwtTokenizer.createAccessToken(userId, new Date());
    }

}
