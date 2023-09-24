package com.example.cognito;

import lombok.Data;

@Data
public class CognitoUserAttributeRequest {
    private String name;
    private String value;
}
