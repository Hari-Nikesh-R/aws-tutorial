package com.example.cognito;

import lombok.Data;

@Data
public class AwsConfirmPasswordRequest {
    private String confirmationCode;
    private String password;
    private String username;
}
