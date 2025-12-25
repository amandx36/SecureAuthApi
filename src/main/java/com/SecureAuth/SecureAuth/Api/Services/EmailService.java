package com.SecureAuth.SecureAuth.Api.Services;


import com.SecureAuth.SecureAuth.Api.model.OTP;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {


    // java provide interface to send emails
    @Autowired
    private JavaMailSender mailSender;


    // Thymeleaf engine used to convert HTML templates
     //  (email-otp.html, welcome-email.html) into final HTML
    @Autowired
    private TemplateEngine templateEngine;

    // comes from application-properties
    @Value("${spring.mail.username}")
    private String fromEmail;


//    toEmail  -> User email where OTP will be sent
//     * @param otpCode  -> Generated OTP code
//     * @param type     -> OTP type (LOGIN / REGISTER / PASSWORD_RESET)
    public  void sendOTPEmail (String toEmail, String otpCode , OTP.OTPType type){
        try{
            // deciding the email subject based on otp type
            String subject = getOTPEmailSubject(type);

            // context is used to used inside emial-otp.html
            Context context = new Context();

            // variable inside the email otp dude
            context.setVariable("otpCode",otpCode);
            context.setVariable("type",type);
            context.setVariable("validityMinutes",5);
            // now convert the email-otp.html into final html string
            String htmlContent = templateEngine.process("email-otp",context);

            // send the final html email
            sendHtmlEmail(toEmail,subject,htmlContent);


        }catch (Exception  e){

            // if email fails then display the error dude
            System.err.println("Failed to send otp email"+e.getMessage());

        }

    }

    private String getOTPEmailSubject(OTP.OTPType type) {
        switch(type){
            case LOGIN :
                return "Your Login Otp Code";
            case REGISTER:
                return "Verify your Email Address";
            case PASSWORD_RESET:
                return "Password is Reset Otp";
            default:
                return "Your verification code";

        }
    }


    // for sending welcome email
    // toEmail :) user email
    // name :) name of user dude
    public  void  sendWelcomeEmail(String toEmail,String name){
        try{
            // create context for welcome-email.html email template
            Context context = new Context();
            context.setVariable("name",name);

            // process welcome email
            String htmlContent = templateEngine.process("welcome-email",context);
            // send  the welcome email
            sendHtmlEmail(toEmail,"Welcome-email",htmlContent);
        }
        catch (Exception e){
        System.err.println("Failed to send welcome email: "+e.getMessage());
            }

    }

    // main method to send the mail using javaMailSender
 private  void sendHtmlEmail(String to , String subject , String htmlContent) throws MessagingException {
// OTP aur Welcome email dono isko call karte hain
     // Create a MIME email message (supports HTML, attachments, etc.)

     MimeMessage message = mailSender.createMimeMessage();
     // MimeMessageHelper
     //true  -> multipart message (future attachments support)
     //UTF-8 -> supports emojis & special characters
     MimeMessageHelper helper = new MimeMessageHelper(message , true , "UTF-8");
     helper.setFrom(fromEmail);     // Sender email
     helper.setTo(to);              // Receiver email
     helper.setSubject(subject);    // Email subject
     helper.setText(htmlContent, true); // true = HTML content
     // send email using smtp server
     mailSender.send(message);

     // log
     System.out.println("Email send to "+to );

 }




}
