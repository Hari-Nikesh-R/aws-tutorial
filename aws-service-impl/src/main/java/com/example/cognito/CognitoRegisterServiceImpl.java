package com.example.cognito;

import com.example.cognito.helper.Util;
import com.example.entity.OneTimePassword;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;


@Service
public class CognitoRegisterServiceImpl implements CognitoRegisterService {
    @Autowired
    CognitoConfig config;
    @Autowired
    private CognitoIdentityProviderClient cognitoUserPool;
    @Autowired
    private OneTimePasswordRepository oneTimePasswordRepository;

    @Autowired
    private Util util;
    @Autowired
    private ObjectMapper mapper;


    @Override
    public ResponseEntity<ApiReturnResponse> register(RegistrationRequest registrationRequest) {
        try {
            String temporaryPassword = new String(generatePassword());
            List<AttributeType> attributeTypeList = new ArrayList<>();
            registrationRequest.getUserAttributes().forEach((attributeType -> {
                attributeTypeList.add(AttributeType.builder().name(attributeType.getName()).value(attributeType.getValue()).build());
            }));
            SignUpRequest signUpRequest = SignUpRequest.builder().clientId(config.getClientId()).password(temporaryPassword).username(registrationRequest.getUsername())
                    .userAttributes(attributeTypeList).build();
            SignUpResponse signUpResult = cognitoUserPool.signUp(signUpRequest);
            return ResponseEntity.status(HttpStatus.OK.value()).body(util.setApiReturnResponse("Registered successfully", true, HttpStatus.OK));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(util.setApiReturnResponse("User not registered", false, HttpStatus.FORBIDDEN));
        }
    }

    @Override
    public ResponseEntity<ApiReturnResponse> verifyCode(String userName, String code) {
        try {
            ConfirmSignUpRequest confirmSignUpRequest = ConfirmSignUpRequest.builder().clientId(config.getClientId()).username(userName).confirmationCode(code).build();
            cognitoUserPool.confirmSignUp(confirmSignUpRequest);
            EmailRequest emailRequest = new EmailRequest();
            emailRequest.setTo(userName);
            emailRequest.setFrom("contact2aravind@gmail.com");
            emailRequest.setBody("Please find your username and password for login:\nUsername: " + userName + "\nPassword: " + retrievePassword(userName) + "\nLogin using this link: https://devapp.veacy.co/");
            emailRequest.setSubject("VeaCy SignIn credentials!");
            String message = sendmail(emailRequest);
            return ResponseEntity.status(HttpStatus.OK.value()).body(util.setApiReturnResponse("Code verified", true, HttpStatus.OK));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(util.setApiReturnResponse("Invalid code", false, HttpStatus.FORBIDDEN));
        }
    }

    private void saveTempPassword(String username, String password) {
        OneTimePassword oneTimePassword = new OneTimePassword();
        oneTimePassword.setPassword(password);
        oneTimePassword.setEmail(username);
        oneTimePasswordRepository.save(oneTimePassword);
        System.out.println("Password save successfully");
    }

    private String retrievePassword(String username) {
        Optional<OneTimePassword> passwordOptional = oneTimePasswordRepository.findByEmail(username);
        return passwordOptional.map(OneTimePassword::getPassword).orElse("Random Password");
    }

    @Override
    public ResponseEntity<ApiReturnResponse> resendVerificationCode(String userName) {
        try {
            if (!cognitoUserPool.adminGetUser(AdminGetUserRequest.builder().userPoolId(config.getUserPoolId()).username(userName).build()).enabled()) {
                cognitoUserPool.resendConfirmationCode(ResendConfirmationCodeRequest.builder().clientId(config.getClientId()).username(userName).build());
                return ResponseEntity.status(HttpStatus.OK.value()).body(util.setApiReturnResponse("Code sent again", true, HttpStatus.OK));
            } else {
                return ResponseEntity.status(HttpStatus.OK.value()).body(util.setApiReturnResponse("User already registered", false, HttpStatus.OK));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(util.setApiReturnResponse("Unable to resend code", false, HttpStatus.FORBIDDEN));
        }
    }

    @Override
    public ResponseEntity<ApiReturnResponse> isVerified(String userName) {
        try {
            AdminGetUserResponse adminGetUserResponse = cognitoUserPool.adminGetUser(AdminGetUserRequest.builder().userPoolId(config.getUserPoolId()).username(userName).build());
            return ResponseEntity.status(HttpStatus.OK.value()).body(util.setApiReturnResponse("{ Enabled: " + adminGetUserResponse.enabled() + "}", true, HttpStatus.OK));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(util.setApiReturnResponse("Something went wrong", false, HttpStatus.FORBIDDEN));
        }
    }

    private String sendmail(EmailRequest emailRequest) {
        try {
//            Destination destination = new Destination().withToAddresses(emailRequest.getTo());
//            Content content = new Content().withData(emailRequest.getBody());
//            Body body = new Body().withText(content);
//            Content subject = new Content().withData(emailRequest.getSubject());
//            Message message = new Message().withBody(body).withSubject(subject);
//            SendEmailRequest sendEmailRequest = new SendEmailRequest().withDestination(destination).withMessage(message).withSource(emailRequest.getFrom());
//            SendEmailResult sendEmailResult = amazonSimpleEmailService.sendEmail(sendEmailRequest);
            final String username = "aravindhanr@piraiinfo.com";
            final String password = "x0xIN96DJ1fTXAjw";

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "mail.smtp2go.com");
            props.put("mail.smtp.port", "2525");

            javax.mail.Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailRequest.getTo()));
            message.setSubject("VeaCy SignIn credentials!");
            message.setText(emailRequest.getBody());
            Transport.send(message);
            return "Mail sent Successfully";
        } catch (Exception exception) {
            exception.printStackTrace();
            return "Mail not sent";
        }
    }


    private char[] generatePassword() {
        String capitalCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialCharacters = "!@#$";
        String numbers = "1234567890";
        String combinedChars = capitalCaseLetters + lowerCaseLetters + specialCharacters + numbers;
        Random random = new Random();
        char[] password = new char[12];

        password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
        password[1] = capitalCaseLetters.charAt(random.nextInt(capitalCaseLetters.length()));
        password[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
        password[3] = numbers.charAt(random.nextInt(numbers.length()));

        for (int i = 4; i < 12; i++) {
            password[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
        }
        return password;
    }
}
