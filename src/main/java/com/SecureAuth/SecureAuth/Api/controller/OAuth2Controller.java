package com.SecureAuth.SecureAuth.Api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/oauth2")
public class OAuth2Controller {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    // for gitHub client id
    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String githubClientId;



    @GetMapping("/config")
    public Map<String, Object> getOAuth2Config() {
        Map<String, Object> config = new HashMap<>();

        // Google OAuth2 URLs
        // redirecting to google for further  verification and link storing in  into authurl
        Map<String, String> google = new HashMap<>();
        google.put("authUrl", "http://localhost:8080/oauth2/authorization/google");
        google.put("clientId", googleClientId);

        // GitHub OAuth2 URLs
        // redirecting to google for further  verification and link storing in  into authurl
        Map<String, String> github = new HashMap<>();
        github.put("authUrl", "http://localhost:8080/oauth2/authorization/github");
        github.put("clientId", githubClientId);

        config.put("google", google);
        config.put("github", github);
        config.put("frontendCallback", "http://localhost:3000/oauth2/callback");

        return config;
    }


    // function for health check dude
    @GetMapping("/health")
    public ResponseEntity<Map<String,Object>> health(){
        Map<String,Object> response = new HashMap<>();
        response.put("status","UP");
        response.put("service","Authentication Service");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);


    }


    // to prevent from hardcoded in frontend so mentioned here  for google
    @GetMapping("/login/google")
    public Map<String, String> googleLogin() {
        Map<String, String> response = new HashMap<>();
        response.put("url", "http://localhost:8080/oauth2/authorization/google");
        return response;
    }

    //  same goes to github

    @GetMapping("/login/github")
    public Map<String, String> githubLogin() {
        Map<String, String> response = new HashMap<>();
        response.put("url", "http://localhost:8080/oauth2/authorization/github");
        return response;


    }

    }