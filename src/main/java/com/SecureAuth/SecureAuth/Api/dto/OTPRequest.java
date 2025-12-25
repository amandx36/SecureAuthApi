package com.SecureAuth.SecureAuth.Api.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OTPRequest {
    @NotBlank  @Email
    private  String email ;

    @NotBlank
    @Size(min =  6 , max =  6 , message = "OTP size must be equal to 6 ")
    private String otp ;

}
