package com.MindSpaceTeam.MindSpace.Controller;

import com.MindSpaceTeam.MindSpace.Components.JWT.OauthProviderMapping;
import com.MindSpaceTeam.MindSpace.Components.JWT.Type.OauthProvider;
import com.MindSpaceTeam.MindSpace.Service.Oauth2UserService;
import org.junit.jupiter.api.BeforeEach;
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
}
