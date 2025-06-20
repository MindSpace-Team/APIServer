package com.MindSpaceTeam.MindSpace.Controller;

import com.MindSpaceTeam.MindSpace.Components.Auth.OauthProviderMapping;
import com.MindSpaceTeam.MindSpace.Components.Auth.Type.OauthProvider;
import com.MindSpaceTeam.MindSpace.Repository.UserRepository;
import com.MindSpaceTeam.MindSpace.Service.Oauth2UserService;
import com.MindSpaceTeam.MindSpace.dto.Login.AccessToken;
import com.MindSpaceTeam.MindSpace.dto.Login.LoginResult;
import com.MindSpaceTeam.MindSpace.dto.Login.RefreshToken;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(OAuthController.class)
class OAuthControllerTest {

    private MockMvc mockMvc;

    @MockitoBean
    private OauthProviderMapping oauth2ProviderMapping;
    @MockitoBean
    private Oauth2UserService oauth2UserService;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(print())
                .build();
    }

    @Test
    void authorizePageTest() throws Exception {
        Mockito.when(oauth2ProviderMapping.getOauthRedirectionUrl(Mockito.eq(OauthProvider.Google), Mockito.anyString()))
                        .thenReturn("http://localhost:80/test-redirect-url");
        mockMvc.perform(get("/oauth2/authorization/{provider}", "google")
                        .header("Accept", "application/json"))
                .andExpect(status().isFound())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andDo(document("oauth2/page/success",
                        pathParameters(
                                parameterWithName("provider").description("OAuth 제공자 (google, naver, kakao 중 하나)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("OAuth 제공자의 인증 페이지 URL")
                        )
                ));
    }

    @Test
    void authorizePageExceptionTest() throws Exception {
        Mockito.when(oauth2ProviderMapping.getOauthRedirectionUrl(Mockito.eq(OauthProvider.Naver), Mockito.anyString()))
                        .thenThrow(IllegalStateException.class);
        mockMvc.perform(get("/oauth2/authorization/{provider}", "github")
                        .header("Accept", "application/json"))
                .andExpect(status().isBadRequest())
                .andDo(document("oauth2/page/exception",
                        pathParameters(
                                parameterWithName("provider").description("Naver, Kakao는 아직 지원하지 않는 기능")
                        )
                ));
    }

    @Test
    @DisplayName("Login 로직 Controller 테스트")
    void loginResultTest() throws Exception{
        String state = "sample-state";
        String authCode = "sample-auth-code";
        String authUser = "0";
        String prompt = "none";
        String refreshTokenValue = "sample-refresh-token";
        String accessTokenValue = "sample-access-token";

        long now = Instant.now().getEpochSecond();
        AccessToken accessToken = new AccessToken(accessTokenValue);
        RefreshToken refreshToken = new RefreshToken(refreshTokenValue, Date.from(Instant.ofEpochSecond(now + 2 * 60 * 60)));
        LoginResult loginResult = new LoginResult(accessToken, refreshToken);
        Mockito.when(this.oauth2UserService.processLogin(Mockito.anyString(), Mockito.any()))
                .thenReturn(loginResult);

        mockMvc.perform(get("/login/oauth2/code/{provider}", "Google")
                        .param("state", state)
                        .param("code", authCode)
                        .param("authuser", authUser)
                        .param("prompt", prompt)
                        .sessionAttr("oauth2_state", state))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenValue))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, Matchers.containsString(refreshTokenValue)))
                .andDo(document("oauth2/page/exception",
                        pathParameters(
                                parameterWithName("provider").description("Oauth Provider(Naver, KaKao는 아직 미지원)")
                        )
                ));
    }
}
