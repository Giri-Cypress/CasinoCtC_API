package com.CasinoCtC.CCtCAPI.security;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // ✅ Secret Key
    private static final String SECRET_KEY =
            "mySecretKeymySecretKeymySecretKey12345";

    // ✅ Access Token Expiration (15 mins)
    private static final long ACCESS_TOKEN_EXPIRATION =
            1000 * 60 * 15;

    // ✅ Refresh Token Expiration (7 days)
    private static final long REFRESH_TOKEN_EXPIRATION =
            1000L * 60 * 60 * 24 * 7;

    // ✅ Signing Key
    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * ✅ Generate Access Token
     */
    public String generateAccessToken(
            String username,
            List<String> roles) {

        Map<String, Object> claims =
                new HashMap<>();

        claims.put("roles", roles);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + ACCESS_TOKEN_EXPIRATION
                        )
                )
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * ✅ Generate Refresh Token
     */
    public String generateRefreshToken(
            String username) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + REFRESH_TOKEN_EXPIRATION
                        )
                )
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * ✅ Extract Username
     */
    public String extractUsername(String token) {

        return getClaims(token).getSubject();
    }

    /**
     * ✅ Extract Roles
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {

        Claims claims = getClaims(token);

        return (List<String>) claims.get("roles");
    }

    /**
     * ✅ Validate Token
     */
    public boolean validateToken(String token) {

        try {

            Claims claims = getClaims(token);

            return claims.getExpiration()
                    .after(new Date());

        } catch (Exception ex) {

            return false;
        }
    }

    /**
     * ✅ Parse Claims
     */
    private Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}