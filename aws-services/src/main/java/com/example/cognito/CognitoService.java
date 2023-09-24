package com.example.cognito;


import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CognitoService {
    ResponseEntity<ApiReturnResponse> changePassword(PasswordRequest changePasswordRequest, String accessToken);

    ResponseEntity<ApiReturnResponse> forgotPassword(String username);
    ResponseEntity<ApiReturnResponse> signOutUser(String username);

    ResponseEntity<ApiReturnResponse> confirmForgotPassword(AwsConfirmPasswordRequest confirmForgotPasswordRequest);

    Map<String, String> signIn(AuthDto authDto);
    Map<String, String> refreshToken(String token);

    ResponseEntity<ApiReturnResponse> isAuthorized(String accessToken) throws JsonProcessingException;

//    ResponseEntity<ApiReturnResponse> sendmail(EmailRequest emailRequest);

    ResponseEntity<ApiReturnResponse> deleteUser(String accessToken);

    ResponseEntity<ApiReturnResponse> disableUser(String userName);

    ResponseEntity<ApiReturnResponse> enableUser(String userName);

    ResponseEntity<ApiReturnResponse> updateUser(List<CognitoUserAttributeRequest> cognitoUserAttributeRequest, String accessToken);

}
