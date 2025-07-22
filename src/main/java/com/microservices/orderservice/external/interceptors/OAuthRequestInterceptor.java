package com.microservices.orderservice.external.interceptors;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

@Configuration
public class OAuthRequestInterceptor implements RequestInterceptor {

    private OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    public OAuthRequestInterceptor(OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
        this.oAuth2AuthorizedClientManager = oAuth2AuthorizedClientManager;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorization", "Bearer " + getToken());
    }

    private String getToken() {
        Authentication principal = new AnonymousAuthenticationToken(
                "internal", "internal", AuthorityUtils.createAuthorityList("ROLE_SYSTEM"));
        OAuth2AuthorizedClient client = oAuth2AuthorizedClientManager.authorize(OAuth2AuthorizeRequest
                .withClientRegistrationId("internal-client")
                .principal(principal)
                .build());
        String tokenValue = client.getAccessToken().getTokenValue();
        return tokenValue;
    }
}
