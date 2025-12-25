package com.SecureAuth.SecureAuth.Api.dto;


import lombok.Data;

@Data
public class AuthResponse {

    private  boolean success ;
    private String message ;
    private  String token ;
    private  Object data ;

    // success response
//    making object using static factory method esme Static Factory Method Pattern ek design pattern hai
//    jisme class apna object khud banati hai
//            aur static method ke through return karti hai.

    // success response
    // using method overloading method 01
    public  static  AuthResponse success (String message){
        AuthResponse response = new AuthResponse();
        response.setSuccess(true);
        response.setMessage(message);
        return response ;

    }
    // method 02
    public  static  AuthResponse  success (String message , String token){
        AuthResponse response = new AuthResponse();
        response.setSuccess(true);
        response.setMessage(message);
        response.setToken(token);
        return response ;


    }
    // Error in response
    public  static  AuthResponse error(String message){
        AuthResponse response = new AuthResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response ;
    }
}
