package com.SecureAuth.SecureAuth.Api.Services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Generates a JWT token after successful authentication
    // The token contains user identity and metadata
    public String generateToken(String email, Long userId, String provider) {

        // Claims = extra information stored inside JWT payload
        Map<String, Object> claims = new HashMap<>();

        // Unique user identifier from database
        claims.put("userId", userId);

        // Authentication source (LOCAL / GOOGLE / GITHUB)
        claims.put("provider", provider);

        // Token generation timestamp
        claims.put("generatedAt", new Date());

        // Build and return the JWT token
        return Jwts.builder()
                .setClaims(claims) // attach custom claims
                .setSubject(email) // main identity email
                .setIssuedAt(new Date()) // token creation time
                .setExpiration( // token expiry time
                        new Date(System.currentTimeMillis() + expiration))
                .signWith( // sign token using secret key
                        getSigningKey(),
                        SignatureAlgorithm.HS256 // HMAC SHA-256 algorithm
                )
                .compact(); // generate final token string
    }

    // Converts the raw secret string into a cryptographic SecretKey
    // This key is used to sign and verify JWT tokens
    private SecretKey getSigningKey() {

        // Convert secret string to byte array and create HMAC key
        return Keys.hmacShaKeyFor(secret.getBytes());
    }


    // this fucniton first verify is this the token using the secret key or not
    // if token is valid , than it extract the email dude
    public  String extractEmail(String token){

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())   // use same secret key to verify token signature
                .build()                          // build the JWT parser
                .parseClaimsJws(token)            // validate token (signature + expiry)
                .getBody()                        // get token payload (claims)
                .getSubject();                    // subject contains the email

    }

    // fucntion to validate the token
    public  boolean validateToken (String token){
        try{
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true ;

        } catch (Exception e ){
            return false;
        }
    }
}
