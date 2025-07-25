package com.MindSpaceTeam.MindSpace.Service;

import com.MindSpaceTeam.MindSpace.Components.Auth.Oauth.Factory.Oauth2RequestAPIFactory;
import com.MindSpaceTeam.MindSpace.Components.Auth.Oauth.Factory.VerifierFactory;
import com.MindSpaceTeam.MindSpace.Components.Auth.Type.OauthProvider;
import com.MindSpaceTeam.MindSpace.Components.JsonMapper;
import com.MindSpaceTeam.MindSpace.Repository.UserRepository;
import com.MindSpaceTeam.MindSpace.Entity.Users;
import com.MindSpaceTeam.MindSpace.Components.Auth.Oauth.API.Oauth2RequestAPI;
import com.MindSpaceTeam.MindSpace.Components.Auth.Oauth.Verifier.JwtVerifier;
import com.MindSpaceTeam.MindSpace.dto.EntityConverter;
import com.MindSpaceTeam.MindSpace.dto.GoogleUserInfoDto;
import com.MindSpaceTeam.MindSpace.dto.JWTToken;
import com.MindSpaceTeam.MindSpace.dto.UserInfoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

import java.util.*;

@Slf4j
@Service
public class Oauth2UserService {
    private final UserRepository userRepository;
    private final EntityConverter entityConverter;
    private final Oauth2RequestAPIFactory oauth2RequestAPIFactory;
    private final VerifierFactory verifierFactory;
    private final JsonMapper jsonMapper;

    public Oauth2UserService(UserRepository userRepository, EntityConverter entityConverter, Oauth2RequestAPIFactory oauth2RequestAPIFactory, VerifierFactory verifierFactory, JsonMapper jsonMapper, RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.entityConverter = entityConverter;
        this.oauth2RequestAPIFactory = oauth2RequestAPIFactory;
        this.verifierFactory = verifierFactory;
        this.jsonMapper = jsonMapper;
    }

    public Users processLogin(String authorizationCode, OauthProvider provider) {
        String authorizationResponse = getAccessToken(authorizationCode, provider);
        try {
            JWTToken jwtToken = extractAndVerifyJwtToken(authorizationResponse, provider);
            return handleUserLogin(jwtToken, provider);
        } catch(JsonProcessingException e) {
            log.warn("Failed to Json parsing", e);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ErrorResponseException e) {
            log.warn("Failed to verify token", e);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e) {
            log.warn("Failed by INTERNAL SERVER ERROR", e);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getAccessToken(String authorizationCode, OauthProvider provider) {
        Oauth2RequestAPI requestAPI = this.oauth2RequestAPIFactory.getAPI(provider);
        String authorizationResponse = null;
        try {
            authorizationResponse = requestAPI.requestOauth2AccessToken(authorizationCode, "authorization_code");
        } catch (Exception e) {
            log.warn(" Provider: {} Access token request failed", provider.getProviderName());
            e.printStackTrace();
        }
        return authorizationResponse;
    }

    private JWTToken extractAndVerifyJwtToken(String accessTokenResponse, OauthProvider provider) throws JsonProcessingException {
        String userJwtToken = this.jsonMapper.toJsonNode(accessTokenResponse).get("id_token").asText();
        JWTToken jwtToken = new JWTToken(userJwtToken);
        JwtVerifier verifier = this.verifierFactory.getVerifier(provider);
        if (!verifier.verify(jwtToken)) {
            log.warn("JWT token verification failed");
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST);
        }
        return jwtToken;
    }

    private Users handleUserLogin(JWTToken jwtToken, OauthProvider provider) throws JsonProcessingException {
        byte[] decodedPayload = Base64.getUrlDecoder().decode(jwtToken.getPayload());
        JsonNode nodes = this.jsonMapper.toJsonNode(new String(decodedPayload));
        // Naver, Kakao Oauth기능 확장 시 추상화 해야함
        UserInfoDto userDto = GoogleUserInfoDto.builder()
                .email(nodes.get("email").asText())
                .name(nodes.get("name").asText())
                .oauthProvider("google")
                .role("USER").build();
        Users user = this.entityConverter.getUserEntity(userDto);
        Optional<Users> foundedUser = this.userRepository.findByEmail(user.getEmail());
        if (foundedUser.isEmpty()) { // Sign up
            log.info("%s User signed up".formatted(user.getEmail()));
            return this.userRepository.save(user);
        } else { // Sign in
            log.info("%s User signed in".formatted(user.getEmail()));
            return user;
        }
    }

}
