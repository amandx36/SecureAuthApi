package com.SecureAuth.SecureAuth.Api.Services;





// This is the boss controller of all the service
// main function dude !!

//Register karwana
//Email verify karwana (OTP)
//Login karwana
//Login ke baad JWT dena

import com.SecureAuth.SecureAuth.Api.model.OTP;
import com.SecureAuth.SecureAuth.Api.model.User;
import com.SecureAuth.SecureAuth.Api.repository.UserRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    // for sata base operation
    @Autowired
    private UserRepository userRepository ;

    // password hashing using  BCrypt implemenation
@Autowired
private PasswordEncoder passwordEncoder;

    // generate otp + verfication
    @Autowired
    private  OTPService otpService ;


    //  JWT token generation service
    @Autowired
    private  JwtService jwtService ;

    // for sending email
    @Autowired
    private EmailService emailService;

    // user registration

    /*
    *   check email exist
    * create usesr
    * hash the password
    * send otp for verification
    *
    * */
    @Transactional
    public  String register (String email , String password , String name ){
        // check is email already register or not via git-hub or google dude
        if(userRepository.existsByEmail(email)){
            return "Email already registered";
        }
        //  2 create new user entity
        User user = new User();
        user.setEmail(email);
        user.setName(name);

        // has the password using the BCrypt
        user.setPassword(passwordEncoder.encode(password));
        // set this is email / git / google
        user.setProvider(User.AuthProvider.LOCAL);

        // mark the email is not verified
        user.setEmailVerified(false);

        // now save to databse
        userRepository.save(user);

        // send otp for emial verfication dude
        otpService.generateAndSendOTP(email, OTP.OTPType.REGISTER);
         return "OTP sent to your email. Please verify it  ";


    }
    // verify registration otp using email
    @Transactional
    public  String verifyRegistration (String email,String otp){
        // verfiy the otp
        if(!otpService.verifyOTP(email,otp)){
            return "Invalid or experied otp";

        }

        // step 02 fetch the user
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("user not found"));

        // mark email is verfied
        user.setEmailVerified(true);
        userRepository.save(user);

        // now send to welcome email
        emailService.sendWelcomeEmail(email, user.getName());
        return "Registration successfully";

    }
    // function for logIn with otp
    public  String login (String email , String password){

        // 1 find user by email
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            return  "User not found ";

        }
        // new User() tab use hota hai jab NAYA user banana ho.
        //userOptional.get() tab use hota hai jab EXISTING user ko DB se laana ho.

        User user = userOptional.get();
        // verify password using BCrypt
        if(!passwordEncoder.matches(password ,user.getPassword())){
            return "Invalid Password";

        }
        // check email is verfied or not
        if(!user.isEmailVerified()){
            return "Email is not verified verify  first";
        }
        // now send the otp for 2FA
        otpService.generateAndSendOTP(email,OTP.OTPType.LOGIN);

        return "OTP Sent to your email";

    }
    // function to get the user by email
    public  Optional <User> getUserByEmail(String email){
        return userRepository.findByEmail(email);


    }
    // function  to get user by id
    public  Optional<User> getUserById(Long id){
        return userRepository.findById(id);
    }

    // verify login otp and generate token
    @Transactional
    public  String verifyLogin(String email , String otp){
        // verify otp
        if(!otpService.verifyOTP(email,otp)){
            return "Invalid OTP";
        }
        // get user
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User not found "));


        // Generate JWT token
        return  jwtService.generateToken(user.getEmail(),user.getId(),user.getProvider().toString());
    }


}
