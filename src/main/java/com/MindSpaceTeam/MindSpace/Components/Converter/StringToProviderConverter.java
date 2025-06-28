package com.MindSpaceTeam.MindSpace.Components.Converter;

import com.MindSpaceTeam.MindSpace.Components.Auth.Type.OauthProvider;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToProviderConverter implements Converter<String, OauthProvider> {
    @Override
    public OauthProvider convert(String providerName) throws IllegalArgumentException {
        return OauthProvider.from(providerName);
    }

    @Override
    public <U> Converter<String, U> andThen(Converter<? super OauthProvider, ? extends U> after) {
        return Converter.super.andThen(after);
    }
}
