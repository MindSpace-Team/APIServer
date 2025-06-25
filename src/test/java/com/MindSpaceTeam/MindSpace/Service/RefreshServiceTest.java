package com.MindSpaceTeam.MindSpace.Service;

import com.MindSpaceTeam.MindSpace.Components.Auth.Token.Exception.RefreshTokenExpiredException;
import com.MindSpaceTeam.MindSpace.Components.Auth.Token.JwtTokenizer;
import com.auth0.jwt.JWT;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class RefreshServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
    private RefreshService refreshService = new RefreshService(redisTemplate);
    private static final String REFRESH_TOKEN = "test-access-token";
    private static final String TEST_SECRET_KEY = "test-secret-key";

    JwtTokenizer jwtTokenizer = new JwtTokenizer(TEST_SECRET_KEY, "mindspace");

    @Test
    @DisplayName("Access Token 재발급 성공 로직 Test")
    void reassignAccessTokenSuccessTest() {
        Mockito.when(redisTemplate.hasKey("refresh: " + REFRESH_TOKEN))
                .thenReturn(true);

        String result = refreshService.refreshAccessToken(REFRESH_TOKEN);

        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("Refresh Token 만료 시 동작 Test")
    void refreshTokenExpiredTest() {
        Mockito.when(redisTemplate.hasKey("refresh: " + REFRESH_TOKEN))
                .thenReturn(false);

        Assertions.assertThatThrownBy(() -> refreshService.refreshAccessToken(REFRESH_TOKEN))
                .isInstanceOf(RefreshTokenExpiredException.class);
    }



}