package com.example.cognito;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CognitoUserResponse {
    private String role;
    private String email;
}
