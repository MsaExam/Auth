package com.sparta.msa_exam.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
    }

    public String generateToken(Duration duration, Long userId, String role) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + duration.toMillis()), userId, role);
    }

    private String makeToken(Date expirationDate, Long userId, String role) {
        return Jwts.builder().issuer(jwtProperties.getIssuer()).issuedAt(new Date()).expiration(expirationDate)
                .claim("id", userId).claim("role", role).signWith(secretKey).compact();
    }

    public boolean validToken(final String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public Authentication getAuthentication(final String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + claims.get("role", String.class)));
        return new UsernamePasswordAuthenticationToken(claims.get("id", Long.class), token,
                authorities);
    }


    private Claims getClaims(String token) {
        Jws<Claims> claimsJws = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        return claimsJws.getPayload();
    }
}