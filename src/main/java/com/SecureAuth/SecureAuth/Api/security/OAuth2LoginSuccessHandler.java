package com.SecureAuth.SecureAuth.Api.security;

import com.SecureAuth.SecureAuth.Api.model.User;
import com.SecureAuth.SecureAuth.Api.model.User.AuthProvider;
import com.SecureAuth.SecureAuth.Api.repository.UserRepository;
import com.SecureAuth.SecureAuth.Api.Services.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

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
    private String frontendUrl;

    // repository to fetch / create user in database
    @Autowired
    private UserRepository userRepository;

    // jwt service to generate our own JWT token
    @Autowired
    private JwtService jwtService;

    // automatically call this method when OAuth2 authentication is successful dude
    //
    //    request  :)  HTTP request
    //     response :) HTTP response
    //     authentication Authentication object (contains OAuth2User)
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        //   01 Get authenticated OAuth2 user
        //  authentication.getPrincipal() returns the logged-in user object
        // For OAuth2, it is always of type OAuth2User
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // adding to data
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // read the required data from attributes
        // email is trusted because it comes from Google / GitHub
        String email = (String) attributes.get("email");

        // provider name (GOOGLE / GITHUB)
        // this tells us whether login came from Google or GitHub
        String provider =
                ((org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) authentication)
                        .getAuthorizedClientRegistrationId()
                        .toUpperCase();

    
        // if user is logging in for the first time via OAuth2
        // then user will NOT exist in database
        // so we create the user automatically
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // first time OAuth2 login -> create new user
            user = new User();
            user.setEmail(email);

            // name comes from provider (Google / GitHub)
            user.setName((String) attributes.get("name"));

            // provider tells us login source
            user.setProvider(AuthProvider.valueOf(provider));

            // providerId is unique id given by Google / GitHub
            // Google -> "sub"
            // GitHub -> "id"
            if (provider.equals("GOOGLE")) {
                user.setProviderId((String) attributes.get("sub"));
            } else if (provider.equals("GITHUB")) {
                user.setProviderId(String.valueOf(attributes.get("id")));
            }

            // email is already verified by OAuth provider
            user.setEmailVerified(true);

            // save user in database
            userRepository.save(user);
        }

        // JWT generation 
   
        // OAuth providers NEVER give JWT for your app
        // backend always generates its own JWT
        String jwtToken = jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                provider
        );

        //  Build frontend redirect URL with query parameters of token
        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendUrl + "/oauth2/callback")
                .queryParam("token", jwtToken)
                .build()
                .toUriString();

        //      Frontend receives JWT via URL
        // Frontend stores JWT (localStorage / cookie)
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
