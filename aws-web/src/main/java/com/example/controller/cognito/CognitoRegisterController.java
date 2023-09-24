package com.example.controller.cognito;


import com.example.cognito.ApiReturnResponse;
import com.example.cognito.CognitoRegisterService;
import com.example.cognito.RegistrationRequest;

import com.example.controller.cognito.helper.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

import static com.example.controller.cognito.helper.Constants.USER_NAME;


@RestController
@CrossOrigin("*")
@RequestMapping(value ="/register")
public class CognitoRegisterController {

    @Autowired
    private CognitoRegisterService cognitoService;

    @PostMapping
    public ResponseEntity<ApiReturnResponse> createUser(@RequestBody RegistrationRequest registrationRequest) {
        return cognitoService.register(registrationRequest);
    }

    @PostMapping(value = "/verify")
    public ResponseEntity<ApiReturnResponse> verifyCode(@PathParam(USER_NAME) String userName, @PathParam(Constants.CODE) String code) {
        return cognitoService.verifyCode(userName, code);
    }

    @GetMapping(value ="/is-confirmed")
    public ResponseEntity<ApiReturnResponse> isConfirmed(@RequestParam(USER_NAME) String userName){
        return cognitoService.isVerified(userName);
    }

    @PostMapping(value = "/resend-confirmation-code")
    public ResponseEntity<ApiReturnResponse> resendVerificationCode(@RequestParam(USER_NAME) String userName) {
        return cognitoService.resendVerificationCode(userName);
    }
}
