package com.microservices.orderservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private static final String ROLES_CLAIM = "https://microservices/com/roles";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(
                authorizeRequest -> authorizeRequest
                        .anyRequest()
                        .authenticated())
                .csrf(CsrfConfigurer::disable)
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(jwtConfigurer -> {
                            jwtConfigurer.jwtAuthenticationConverter(jwsConverter());
                        }))
                .build();
    }

    private JwtAuthenticationConverter jwsConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
            Collection<GrantedAuthority> grantedAuthorities = grantedAuthoritiesConverter.convert(jwt);
            Collection<GrantedAuthority> customAuthorities = jwt.getClaimAsStringList(ROLES_CLAIM).stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role)).collect(Collectors.toList());
            grantedAuthorities.addAll(customAuthorities);
            return grantedAuthorities;
        });
        return converter;
    }
}
