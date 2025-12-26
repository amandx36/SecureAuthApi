package com.SecureAuth.SecureAuth.Api.security;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.Map;

//
//This class is triggered automatically AFTER successful OAuth2 login
//   (Google / GitHub).
//
//         Its main job is:
//            1. Extract user info + JWT from OAuth2User
//     2. Redirect the user to frontend with JWT token
//
//       This acts as a bridge between:
//        Spring Security OAuth2 backend  --->  Frontend application
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // Front End url
    //   injected from application.yml

    // assume base url is this https://localhost:300
    @Value("${frontend.url:http://localhost:3000}")

    private  String frontendUrl;


    // automatically call this method when OAuth2 authentication is successful dude
//
//    request  :)  HTTP request
//     response :) HTTP response
//     authentication Authentication object (contains OAuth2User)
    @Override
    public  void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response , Authentication authentication) throws IOException , ServletException {

        //   01 Get authenticated OAuth2 user
       //  authentication.getPrincipal() returns the logged-in user object
        // For OAuth2, it is always of type OAuth2User

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // adding to data
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // read the required data from attributes
        String email = (String)  attributes.get("email");
        String jwtToken = (String) attributes.get("jwt");
        Long userId = (Long) attributes.get("userId");
       //  Build frontend redirect URL with query parameters of token email userId to front in url

        String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl+"/oauth2/callback").queryParam("token",jwtToken)
                .queryParam("email",email).queryParam("userId",userId).build().toUriString();

   //      Frontend receives JWT via URL
                // Frontend stores JWT (localStorage / cookie)
    getRedirectStrategy().sendRedirect(request,response,redirectUrl);
    }

}
