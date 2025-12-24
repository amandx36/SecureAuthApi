package com.SecureAuth.SecureAuth.Api.model;

import lombok.Data;

import java.util.Map;

@Data

public class OAuth2UserInfo {
    private String id;
    private String email;
    private String name;
    private String imageUrl;
    private String provider;

    // now formating the data which is send by google to store in our databse
    public static OAuth2UserInfo fromGoogle(Map<String, Object> attributes) {
        OAuth2UserInfo info = new OAuth2UserInfo(); // best method for allocate by object otherwise hamme individual
                                                    // create karna hoga dude !!
        info.setId((String) attributes.get("sub"));
        info.setEmail((String) attributes.get("email"));
        info.setName((String) attributes.get("name"));
        info.setImageUrl((String) attributes.get("picture"));
        info.setProvider("GOOGLE");
        return info;
    }

    // factory method for git-hub dude

    // Map <String,Object> key is String but value may be integer , boolean so form
    // prevent this we use Object dude for type saftery
    public static OAuth2UserInfo fromGitHub(Map<String, Object> attributes) {
        OAuth2UserInfo info = new OAuth2UserInfo();
        info.setId(String.valueOf(attributes.get("id")));

        // git-hub may return null value so adding condition
        String email = (String) attributes.get("email");
        if (email == null) {
            // if email is not provided by git-hub than ectract the name and make fake email
            // to store in database user did not get emial later
            email = attributes.get("login") + "@github.com";
            info.setEmail(email);

        }
        info.setName((String) attributes.get("name"));
        info.setImageUrl((String) attributes.get("avatar_url"));
        info.setProvider("GITHUB");

        return info;

    }
}
