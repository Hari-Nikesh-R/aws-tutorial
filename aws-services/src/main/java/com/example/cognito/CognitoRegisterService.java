package com.example.cognito;


import org.springframework.http.ResponseEntity;

public interface CognitoRegisterService {
    ResponseEntity<ApiReturnResponse> register(RegistrationRequest registrationRequest);
    ResponseEntity<ApiReturnResponse> verifyCode(String userName, String code);

    ResponseEntity<ApiReturnResponse> resendVerificationCode(String userName);
    ResponseEntity<ApiReturnResponse> isVerified(String userName);

}
