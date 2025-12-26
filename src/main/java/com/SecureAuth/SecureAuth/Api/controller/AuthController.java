package com.SecureAuth.SecureAuth.Api.controller;


import com.SecureAuth.SecureAuth.Api.Services.AuthService;
import com.SecureAuth.SecureAuth.Api.dto.LoginRequest;
import com.SecureAuth.SecureAuth.Api.dto.OTPRequest;
import com.SecureAuth.SecureAuth.Api.dto.RegisterRequest;
import com.SecureAuth.SecureAuth.Api.model.User;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// for email and password !!
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
///   authService responsible for  password hasing otp genreate and validate jwt creation and databse operation
    @Autowired
    private AuthService authService ;


    @PostMapping("/register")
    public ResponseEntity <Map<String, Object>> register(  @Valid  @RequestBody RegisterRequest request){
        try{
            String result = authService.register(
                    request.getEmail(),
                    request.getPassword(),
                    request.getName());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);

            return ResponseEntity.ok(response);

        }catch (Exception e ){

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
    // making a function flow is given dude
    // user submit email + otp
    // otp validated  -> user account is activated -> it prevent creation of fake account -> confirm the ownership of account
    @PostMapping("/verify-registration")
    public  ResponseEntity<Map<String , Object >> verifyRegistration (@Valid @RequestBody OTPRequest request){
        try {
            String result  = authService.verifyRegistration(request.getEmail(),request.getOtp());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);

            return ResponseEntity.ok(response);
        } catch (Exception e ){
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    // function for login user validate email and password -> if correct generate otp -> send otp  to email
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request){
        try{
            String result = authService.login(  request.getEmail(),
                    request.getPassword());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);

            return ResponseEntity.ok(response);
        } catch (Exception e ){
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }

    }
// flow
    // validate otp -> generate jwt token -> return jwt to client
    // Most critical -> if i do any bug account will take over dude

    @PostMapping("/verify-login")
    public ResponseEntity<Map<String, Object>> verifyLogin(
            @Valid @RequestBody OTPRequest request) {

        try {
            String token = authService.verifyLogin(
                    request.getEmail(),
                    request.getOtp()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("token", token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }


    // fetch user by email

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(
            @RequestParam String email) {

        try {
            var userOptional = authService.getUserByEmail(email);

            if (userOptional.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "User not found");
                return ResponseEntity.status(404).body(response);
            }

            User user = userOptional.get();

            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("email", user.getEmail());
            userData.put("name", user.getName());
            userData.put("profilePicture", user.getProfilePicture());
            userData.put("provider", user.getProvider());
            userData.put("emailVerified", user.isEmailVerified());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", userData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

//
//    Used for:
//            * - Monitoring
//      Load balancers
//      DevOps health probes
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {

        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Authentication Service");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }







}
