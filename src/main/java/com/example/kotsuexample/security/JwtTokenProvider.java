package com.example.kotsuexample.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final SecretKey secretKey;
    private static final Duration ACCESS_TOKEN_EXPIRATION_TIME = Duration.ofMinutes(60); // 1시간

    public JwtTokenProvider(@Value("${jwt.secret}") String jwtSecret) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtSecret));
    }

    public String createAccessToken(String userId) {
        Date nowTime = new Date(System.currentTimeMillis());
        Date expirationTime = new Date(nowTime.getTime() + ACCESS_TOKEN_EXPIRATION_TIME.toMillis());

        return Jwts.builder()
                .subject(userId) // setSubject -> subject
                .issuedAt(nowTime)  // 발급 시간 설정
                .expiration(expirationTime)  // 만료 시간 설정
                .signWith(secretKey)  // 서명
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public static Duration getAccessTokenExpirationTime() {
        return ACCESS_TOKEN_EXPIRATION_TIME;
    }
}
