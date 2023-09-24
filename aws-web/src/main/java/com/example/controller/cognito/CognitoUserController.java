package com.example.controller.cognito;

import com.example.cognito.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Map;

import static com.example.controller.cognito.helper.Constants.USER_NAME;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@CrossOrigin("*")
@RequestMapping(value = "/cognito-idp")
public class CognitoUserController {

    @Autowired
    private CognitoService cognitoService;

    @Autowired
    private ObjectMapper mapper;

    @PostMapping(value = "/change-password")
    public ResponseEntity<ApiReturnResponse> changePassword(@RequestBody PasswordRequest changePasswordRequest, @RequestHeader("Authorization") String accessToken) {
        return cognitoService.changePassword(changePasswordRequest, accessToken);
    }

    @PostMapping(value = "/sign-in")
    public ResponseEntity<Map<String, String>> signIn(@RequestBody AuthDto authDto) {
        return ResponseEntity.ok(cognitoService.signIn(authDto));
    }

    @GetMapping(value = "/sign-out")
    public ResponseEntity<ApiReturnResponse> signOut(@RequestParam(USER_NAME) String username) {
        return cognitoService.signOutUser(username);
    }

    @GetMapping(value = "/forgot-password")
    public ResponseEntity<ApiReturnResponse> forgotPassword(@PathParam(USER_NAME) String userName) {
        return cognitoService.forgotPassword(userName);
    }

    @PostMapping(value = "/confirm-forgot-password")
    public ResponseEntity<ApiReturnResponse> confirmForgotPassword(@RequestBody AwsConfirmPasswordRequest confirmForgotPasswordRequest) {
        return cognitoService.confirmForgotPassword(confirmForgotPasswordRequest);
    }

    @DeleteMapping(value = "/delete-user")
    public ResponseEntity<ApiReturnResponse> deleteCognitoUser(@RequestHeader(AUTHORIZATION) String accessToken) {
        return cognitoService.deleteUser(accessToken.substring(7));
    }

    @GetMapping(value = "is-authorized")
    public ResponseEntity<ApiReturnResponse> isAuthorized(@RequestHeader(AUTHORIZATION) String accessToken) throws JsonProcessingException {
        return cognitoService.isAuthorized(accessToken);
    }

//    @PostMapping(value = "/send-mail")
//    public ResponseEntity<ApiReturnResponse> sendEmail(@RequestBody EmailRequest emailRequest) {
//        return cognitoService.sendmail(emailRequest);
//    }

    @PutMapping(value = "/update-user")
    public ResponseEntity<ApiReturnResponse> updateUser(@RequestHeader(AUTHORIZATION) String accessToken, @RequestBody List<CognitoUserAttributeRequest> cognitoUserAttributeRequestList) {
        return cognitoService.updateUser(cognitoUserAttributeRequestList, accessToken.substring(7));
    }

    @DeleteMapping(value = "/disable-user")
    public ResponseEntity<ApiReturnResponse> disableUser(@RequestParam(USER_NAME) String userName) {
        return cognitoService.disableUser(userName);
    }

    @PostMapping(value = "/enable-user")
    public ResponseEntity<ApiReturnResponse> enableUser(@RequestParam(USER_NAME) String userName) {
        return cognitoService.enableUser(userName);
    }

    @PostMapping(value = "/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestHeader(AUTHORIZATION) String refreshToken) {
        return ResponseEntity.ok(cognitoService.refreshToken(refreshToken));
    }
}
