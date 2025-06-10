package com.MindSpaceTeam.MindSpace.Service;

import com.MindSpaceTeam.MindSpace.Repository.UserRepository;
import com.MindSpaceTeam.MindSpace.Entity.Users;
import com.MindSpaceTeam.MindSpace.Service.Result.LoginResult;
import com.MindSpaceTeam.MindSpace.dto.EntityConverter;
import com.MindSpaceTeam.MindSpace.dto.GoogleUserInfoDto;
import com.MindSpaceTeam.MindSpace.dto.UserInfoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

import java.util.Optional;

@Service
public class Oauth2UserService {
    private final UserRepository userRepository;
    private final EntityConverter entityConverter;
    private final OauthJwtService oauthJwtService;

    public Oauth2UserService(UserRepository userRepository, EntityConverter entityConverter, OauthJwtService oauthJwtService) {
        this.userRepository = userRepository;
        this.entityConverter = entityConverter;
        this.oauthJwtService = oauthJwtService;
    }

    public LoginResult processLogin(String authorizationCode) {
        try {
            String authorizationResponse = this.oauthJwtService.requestJwtToken(authorizationCode, "authorization_code");
            String userJwtToken = this.oauthJwtService.getProfileToken(authorizationResponse);
//            if (!this.oauthJwtService.verifyToken(userJwtToken)) {
//                throw new ErrorResponseException(HttpStatus.BAD_REQUEST);
//            }
            JsonNode nodes = this.oauthJwtService.getPayLoadJsonNode(userJwtToken);
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
