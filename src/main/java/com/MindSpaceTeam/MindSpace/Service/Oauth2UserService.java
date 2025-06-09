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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;

@Service
public class Oauth2UserService {
    private UserRepository userRepository;
    private EntityConverter entityConverter;
    @Value("spring.security.oauth2.client.registration.google.client-secret")
    private String clientSecret;

    public Oauth2UserService(UserRepository userRepository, EntityConverter entityConverter) {
        this.userRepository = userRepository;
        this.entityConverter = entityConverter;
    }

    // Todo: Refactoring필요
    public LoginResult processLogin(String bodyData) {
        try {
            String userInfoJwtToken = getProfileToken(bodyData);
            String userInfo = parseProfileJWTToken(userInfoJwtToken);
            // DTO변환
            ObjectMapper mapper = new ObjectMapper();
            JsonNode nodes = mapper.readTree(userInfo);
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

    public String getProfileToken(String bodyData) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(bodyData);
        return node.get("id_token").asText();
    }

    public String parseProfileJWTToken(String jwtToken) {
        String[] sections = jwtToken.split("\\.");
        String payload = sections[1];
        Base64.Decoder decoder = Base64.getDecoder();
        String infos = new String(decoder.decode(payload));
        return infos;
    }
}
