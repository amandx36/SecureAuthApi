package com.SecureAuth.SecureAuth.Api.Services;


import com.SecureAuth.SecureAuth.Api.model.OTP;
import com.SecureAuth.SecureAuth.Api.repository.OTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OTPService {
    // for otp database operations
    @Autowired
    private OTPRepository otpRepository;

    // for email services
    @Autowired
    private  EmailService emailService;

    // otp validation time dude
    private  static  final int OTP_VALIDITY_MINUTES = 5 ;

    // function for generating otp
    public  String generateAndSendOTP(String email , OTP.OTPType type){
        // step 01 delete the previous otp
        otpRepository.deleteByEmail(email);

        // step 02 generate random otp
        String otpCode = generateRandomOTP(6);

        // create the otp entity and than save it the the database
        OTP otp = new OTP() ;
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setType(type);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
        // save to the repository
        otpRepository.save(otp);
        // send otp to user using mail
        emailService.sendOTPEmail(email,otpCode,type);
        System.out.println("OTP send to user ");
        return  otpCode;
    }


private  String generateRandomOTP(int x ){
    // SecureRandom provides cryptographically strong random numbers
    SecureRandom random = new SecureRandom();
    StringBuilder otp = new StringBuilder();

    // generate random digit
    for (int i = 0 ; i < x ; i++){
        otp.append(random.nextInt(10)) ; // genrate random number between 0 to 10

    }
    return  otp.toString();
    }

    //  function for    Verify OTP entered by the user
    public  boolean verifyOTP(String email , String otpCode){
        // fetch otp from database & email must match & otp must matched
        Optional <OTP> otpOptional = otpRepository.findByEmailAndOtpCodeAndUsedFalse(email,otpCode);

        // check is the otp  empty
        if(otpOptional.isEmpty()){
        //     System.out.println("OTP not found for "+email+"!!");
            return false ;


        }
        // getting the otp
        OTP otp = otpOptional.get();
        // checking otp is expired or not
        if(otp.isExpired()){
          //   System.out.println("OTP is expired for email "+email+"!!");
            return false ;

        }
        // marked this otp is used
        otp.setUsed(true);
        otpRepository.save(otp);
     //   System.out.println("otp verified");
        return  true ;

    }




    // method to resend otp dude
    public  String resendOTP(String email , OTP.OTPType type ){
        // bascially gnerating the old otp
        return  generateAndSendOTP(email , type);
    }
}

