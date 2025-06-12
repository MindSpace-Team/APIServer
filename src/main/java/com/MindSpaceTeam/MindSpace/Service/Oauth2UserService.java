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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

import java.util.Base64;
import java.util.Optional;

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
        try {
            Oauth2RequestAPI requestAPI = this.oauth2RequestAPIFactory.getAPI(provider);
            String authorizationResponse = requestAPI.requestOauth2AccessToken(authorizationCode, "authorization_code");

            String userJwtToken = this.jsonMapper.toJsonNode(authorizationResponse).get("id_token").asText();
            JWTToken jwtToken = new JWTToken(userJwtToken);
            JwtVerifier verifier = this.verifierFactory.getVerifier(provider);
            if (!verifier.verify(jwtToken)) {
                throw new ErrorResponseException(HttpStatus.BAD_REQUEST);
            }
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
                this.userRepository.save(user);
                return LoginResult.SIGN_UP_SUCCESS;
            } else {
                return LoginResult.LOGIN_SUCCESS;
            }
        } catch(JsonProcessingException e) {
            return LoginResult.SERVER_ERROR;
        }
    }
}
