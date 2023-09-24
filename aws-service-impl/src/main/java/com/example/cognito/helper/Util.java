/*
 * Copyright (C) 2023-2024 Kaytes Pvt Ltd. The right to copy, distribute, modify, or otherwise
 * make use of this software may be licensed only pursuant to the terms of an applicable Kaytes Pvt Ltd license agreement.
 */
package com.example.cognito.helper;

import com.example.cognito.ApiGetReturnResponse;
import com.example.cognito.ApiReturnResponse;
import com.example.cognito.CognitoUserResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserResponse;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

/**
 * The Util class provides an implementation of the Responses interface,
 * handling CRUD operations and other actions on All ServiceImpl.
 */

@Service
public class Util {

    @Autowired
    private CognitoIdentityProviderClient cognitoUserPool;

    private final ApiReturnResponse apiReturnResponse = new ApiReturnResponse();
    private final ApiGetReturnResponse apiGetReturnResponse = new ApiGetReturnResponse();

    /**
     * The setApiReturnResponse method returns an ApiReturnResponse with the given
     * message,status,code.
     */
    public ApiReturnResponse setApiReturnResponse(String message, Boolean status, HttpStatus code) {
        apiReturnResponse.setMessage(message);
        apiReturnResponse.setStatus(status);
        apiReturnResponse.setStatusCode(code.value());
        return apiReturnResponse;
    }

    /**
     * The setApiReturnResponse method returns an ApiGetReturnResponse with the
     * given message,status,code,retrivedResult.
     */
    public ApiGetReturnResponse setApiGetReturnResponse(String message, Boolean status, HttpStatus code,
                                                        List<Object> retrivedResult) {
        apiGetReturnResponse.setMessage(message);
        apiGetReturnResponse.setStatus(status);
        apiGetReturnResponse.setStatusCode(code.value());
        apiGetReturnResponse.setRetrievedResult(retrivedResult);
        return apiGetReturnResponse;
    }

    public static Boolean validatePhoneNumber(String phoneNumber) {
        return Pattern.compile("^(\\+[0-9]{8,}|[0-9]{0,9})$").matcher(phoneNumber).matches();
    }

    public static String encryptString(byte[] input) {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            KeyPair pair = keyPairGen.generateKeyPair();
            PublicKey publicKey = pair.getPublic();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            cipher.update(input);
            byte[] cipherText = cipher.doFinal();
            return new String(cipherText, StandardCharsets.UTF_8);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static String decryptedString(byte[] cipherText) {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            KeyPair pair = keyPairGen.generateKeyPair();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, pair.getPrivate());
            byte[] decipheredText = cipher.doFinal(cipherText);
            System.out.println(new String(decipheredText));
            return new String(decipheredText);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public CognitoUserResponse getCognitoUserDetails(String accessToken) {
        GetUserRequest getUserRequest = GetUserRequest.builder().accessToken(accessToken.substring(7)).build();
        AtomicReference<String> role = new AtomicReference<>("");
        AtomicReference<String> email = new AtomicReference<>("");
        GetUserResponse getUserResult = cognitoUserPool.getUser(getUserRequest);
        System.out.println(getUserResult.username());
        getUserResult.userAttributes().forEach((attributeType) -> {
            if (attributeType.name().equals("custom:role")) {
                role.set(attributeType.value());
            }
            if (attributeType.name().equals("email")) {
                email.set(attributeType.value());
            }
        });
        return CognitoUserResponse.builder().email(email.get()).role(role.get()).build();
    }

}