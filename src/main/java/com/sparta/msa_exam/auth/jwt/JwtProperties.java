package com.sparta.msa_exam.auth.jwt;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@ConfigurationProperties("service.jwt")
public class JwtProperties {
    private final String issuer;
    private final String secret;
    private final Long expiration;

    @ConstructorBinding
    public JwtProperties(String issuer, String secret, Long expiration) {
        this.issuer = issuer;
        this.secret = secret;
        this.expiration = expiration;
    }
}