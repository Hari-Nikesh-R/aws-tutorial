package com.example.cognito;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Data
@Configuration
public class CognitoConfig {

    @Value("${aws.cognito.region}")
    private String awsRegion;

    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;

    @Value("${aws.cognito.clientId}")
    private String clientId;

    @Value("${aws.cognito.accessKey}")
    private String accessKey;

    @Value("${aws.cognito.secretKey}")
    private String secretKey;



    @Bean
    public CognitoIdentityProviderClient getAWSCognitoIdentityProvider() {
        return CognitoIdentityProviderClient.builder().credentialsProvider(() -> AwsBasicCredentials.create(accessKey, secretKey)).region(Region.of(awsRegion)).build();

    }


}
