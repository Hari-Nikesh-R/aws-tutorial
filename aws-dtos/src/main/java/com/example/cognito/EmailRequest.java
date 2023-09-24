package com.example.cognito;

import lombok.Data;

@Data
public class EmailRequest {
    private String to;
    private String from;
    private String subject;
    private String body;
}
