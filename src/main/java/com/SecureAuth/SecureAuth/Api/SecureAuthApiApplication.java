package com.SecureAuth.SecureAuth.Api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SecureAuthApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureAuthApiApplication.class, args);

        System.out.println("Secure Auth Api  " +
                "Email Otp Based " +
                "Google OAuth2 " +
                "GitHub OAuth2 ");

	}

}
