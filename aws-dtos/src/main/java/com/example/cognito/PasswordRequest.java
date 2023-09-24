package com.example.cognito;

import lombok.Data;

@Data
public class PasswordRequest {
    private String previousPassword;
    private String proposedPassword;
}
