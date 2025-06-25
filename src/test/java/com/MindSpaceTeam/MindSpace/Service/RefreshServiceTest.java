package com.MindSpaceTeam.MindSpace.Service;

import com.MindSpaceTeam.MindSpace.Components.Auth.Token.Exception.RefreshTokenExpiredException;
import com.MindSpaceTeam.MindSpace.Components.Auth.Token.JwtTokenizer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class RefreshServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private HashOperations<String, String, String> hOps;
    JwtTokenizer jwtTokenizer;
    private RefreshService refreshService;
    private static final String REFRESH_TOKEN = "test-access-token";
    private static final String TEST_SECRET_KEY = "test-secret-key";
    private static final String TEST_USER_ID = "test-user-id";

    @BeforeEach
    void setUp() {
        Mockito.<HashOperations<String, String, String>>when(redisTemplate.opsForHash())
                .thenReturn(hOps);
        jwtTokenizer = new JwtTokenizer(TEST_SECRET_KEY, "mindspace");
        refreshService = new RefreshService(redisTemplate, jwtTokenizer);
    }

    @Test
    @DisplayName("Access Token 재발급 성공 로직 Test")
    void reassignAccessTokenSuccessTest() {
        Mockito.when(redisTemplate.hasKey("refresh: " + REFRESH_TOKEN))
                .thenReturn(true);
        Mockito.when(hOps.get("refresh: %s".formatted(REFRESH_TOKEN), "userID"))
                .thenReturn(TEST_USER_ID);

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