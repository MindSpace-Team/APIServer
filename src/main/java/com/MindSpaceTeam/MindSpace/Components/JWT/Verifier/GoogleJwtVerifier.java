package com.MindSpaceTeam.MindSpace.Components.JWT.Verifier;

import com.MindSpaceTeam.MindSpace.Components.JWT.API.GoogleOauthAPI;
import com.MindSpaceTeam.MindSpace.Components.JWT.API.Oauth2RequestAPI;
import com.MindSpaceTeam.MindSpace.Components.JWT.Type.OauthProvider;
import com.MindSpaceTeam.MindSpace.dto.JWTToken;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponseException;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Slf4j
@Component
public class GoogleJwtVerifier implements JwtVerifier{

    private final Oauth2RequestAPI oauth2RequestAPI;

    public GoogleJwtVerifier(GoogleOauthAPI googleOauthAPI) {
        this.oauth2RequestAPI = googleOauthAPI;
    }

    @Override
    public OauthProvider getOauthProvider() {
        return OauthProvider.Google;
    }

    @Override
    public boolean verify(JWTToken token) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String headerInfo = new String(decoder.decode(token.getHeader()));
        try {
            PublicKey publicKey = getPublicKey(headerInfo);
            String signedData = token.getHeader() + "." + token.getPayload();

            byte[] signatureBytes = Base64.getUrlDecoder().decode(token.getSignature());
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(signedData.getBytes());
            return sig.verify(signatureBytes);
        } catch (Exception e) {
            log.warn("JWT Token 검증 실패", e);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private PublicKey getPublicKey(String headerInfo) throws Exception {
        JsonNode myKey = oauth2RequestAPI.requestPublicKeys(headerInfo);
        String n_base64url = myKey.get("n").asText();
        String e_base64url = myKey.get("e").asText();

        byte[] nBytes = Base64.getUrlDecoder().decode(n_base64url);
        byte[] eBytes = Base64.getUrlDecoder().decode(e_base64url);

        BigInteger modulus = new BigInteger(1, nBytes);
        BigInteger exponent = new BigInteger(1, eBytes);

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}
