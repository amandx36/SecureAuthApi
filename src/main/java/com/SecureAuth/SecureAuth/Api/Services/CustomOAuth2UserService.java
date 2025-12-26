package com.SecureAuth.SecureAuth.Api.Services;


import com.SecureAuth.SecureAuth.Api.model.OAuth2UserInfo;
import com.SecureAuth.SecureAuth.Api.model.User;
import com.SecureAuth.SecureAuth.Api.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomOAuth2UserService  extends DefaultOAuth2UserService {

    // for database operation
    @Autowired
    private UserRepository userRepository ;

    // for generating the jwt token
    @Autowired
    private  JwtService jwtService ;

    // spring call this method when git or google is log in correctly
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // OAuth se user info lao like google and github
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // storing in map after getting the user info
        Map<String,Object> attributes = oAuth2User.getAttributes();

        // identify the user name like google  or github
        String provider = userRequest
                .getClientRegistration()
                .getRegistrationId();


        // calling the function jo mere data ko common fomat main le aeega
        OAuth2UserInfo userInfo =  processOAuth2User(provider,attributes);


        // save or update the user
        User user = saveOrUpdate(userInfo);

        // generate the jwt token (stateLess auth ke liye )
        String jwt = jwtService.generateToken(user.getEmail(),user.getId()
                ,user.getProvider().toString());


        // existing attributes + custom data
        Map<String , Object> newAttributes = new HashMap<>(attributes);
        newAttributes.put("jwt",jwt);
        newAttributes.put("userId",user.getId());
        newAttributes.put("email",user.getEmail());

        // return the Authenticated data
        String nameAttributeKey =
                provider.equals("github") ? "id" : "email";

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                newAttributes,
                nameAttributeKey
        );


    }

    private OAuth2UserInfo processOAuth2User(String provider, Map<String, Object> attributes) {
        // if google hai to common format main convert karo using inbuilt function
        if("google".equals(provider)){
            // converting the google attributes in my common format

            return OAuth2UserInfo.fromGoogle(attributes);
        }
        else if("github".equals(provider)){
            // converting the github attributes to common my    format
            return OAuth2UserInfo.fromGitHub(attributes);

        }
        else {
            // unknown provider
            throw new OAuth2AuthenticationException("Unsupported provider "+ provider);
        }
    }

    // method for saving and updating the data to the datase
    private  User saveOrUpdate (OAuth2UserInfo userInfo){
        // converting the provider  in our enum
        User.AuthProvider provider = User.AuthProvider.valueOf(userInfo.getProvider());
        // check user pahle se database main hai kya
        User existingUser = userRepository.findByProviderAndProviderId(provider,userInfo.getId()).orElse(null);

        // if user already exist latest info ko update kar do
        if (existingUser != null){
            // update it
            existingUser.setName(userInfo.getName());
            existingUser.setProfilePicture(userInfo.getImageUrl());
            existingUser.setEmailVerified(true);
            return userRepository.save(existingUser);
        }
        //  if new auth user hai to fir manually attribute set and save it dude
        User newUser = new User();
        newUser.setEmail(userInfo.getEmail());
        newUser.setName(userInfo.getName());
        newUser.setProfilePicture(userInfo.getImageUrl());
        newUser.setProviderId(userInfo.getId());
        newUser.setEmailVerified(true);
        newUser.setProvider(provider);


        // OAuth user ke liye dummy password save kar lo
        newUser.setPassword("OAUTH@_"+System.currentTimeMillis()+"i see you ");

    return userRepository.save(newUser);

    }

}
