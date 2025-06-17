package com.MindSpaceTeam.MindSpace.Service;

import com.MindSpaceTeam.MindSpace.Components.JWT.Factory.Oauth2RequestAPIFactory;
import com.MindSpaceTeam.MindSpace.Components.JWT.Factory.VerifierFactory;
import com.MindSpaceTeam.MindSpace.Components.JWT.Type.OauthProvider;
import com.MindSpaceTeam.MindSpace.Components.JsonMapper;
import com.MindSpaceTeam.MindSpace.Repository.UserRepository;
import com.MindSpaceTeam.MindSpace.Entity.Users;
import com.MindSpaceTeam.MindSpace.Components.JWT.API.Oauth2RequestAPI;
import com.MindSpaceTeam.MindSpace.Components.JWT.Verifier.JwtVerifier;
import com.MindSpaceTeam.MindSpace.Service.Result.LoginResult;
import com.MindSpaceTeam.MindSpace.dto.EntityConverter;
import com.MindSpaceTeam.MindSpace.dto.GoogleUserInfoDto;
import com.MindSpaceTeam.MindSpace.dto.JWTToken;
import com.MindSpaceTeam.MindSpace.dto.UserInfoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
public class Oauth2UserService {
    private final UserRepository userRepository;
    private final EntityConverter entityConverter;
    private final Oauth2RequestAPIFactory oauth2RequestAPIFactory;
    private final VerifierFactory verifierFactory;
    private final JsonMapper jsonMapper;

    public Oauth2UserService(UserRepository userRepository, EntityConverter entityConverter, Oauth2RequestAPIFactory oauth2RequestAPIFactory, VerifierFactory verifierFactory, JsonMapper jsonMapper) {
        this.userRepository = userRepository;
        this.entityConverter = entityConverter;
        this.oauth2RequestAPIFactory = oauth2RequestAPIFactory;
        this.verifierFactory = verifierFactory;
        this.jsonMapper = jsonMapper;
    }

    public LoginResult processLogin(String authorizationCode, OauthProvider provider) {
        String authorizationResponse = getAccessToken(authorizationCode, provider);
        try {
            JWTToken jwtToken = extractAndVerifyJwtToken(authorizationResponse, provider);
            return handleUserLogin(jwtToken, provider);
        } catch(JsonProcessingException e) {
            log.warn("Failed to Json parsing", e);
            return LoginResult.SERVER_ERROR;
        } catch (ErrorResponseException e) {
            log.warn("Failed to verify token", e);
            return LoginResult.LOGIN_FAILED;
        }
        catch(Exception e) {
            log.warn("Failed by INTERNAL SERVER ERROR", e);
            return LoginResult.SERVER_ERROR;
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

    private LoginResult handleUserLogin(JWTToken jwtToken, OauthProvider provider) throws JsonProcessingException {
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
        if (foundedUser.isEmpty()) {
            log.info("%s User signed up".formatted(user.getEmail()));
            this.userRepository.save(user);
            return LoginResult.SIGN_UP_SUCCESS;
        } else {
            log.info("%s User signed in".formatted(user.getEmail()));
            return LoginResult.LOGIN_SUCCESS;
        }
    }
}
