package com.MindSpaceTeam.MindSpace.Service;

import com.MindSpaceTeam.MindSpace.Repository.UserRepository;
import com.MindSpaceTeam.MindSpace.dto.Users;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Oauth2UserService extends DefaultOAuth2UserService {
    private UserRepository userRepository;

    public Oauth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Users user = Users.builder().email(oAuth2User.getAttribute("email"))
                        .name(oAuth2User.getAttribute("name"))
                                .oauthProvider("google")
                                        .role("USER").build();
        Optional<Users> foundedUser = this.userRepository.findByEmail(user.getEmail());
        if (foundedUser.isEmpty()) {
            this.userRepository.save(user);
            this.userRepository.flush();
        }
        return super.loadUser(userRequest);
    }
}
