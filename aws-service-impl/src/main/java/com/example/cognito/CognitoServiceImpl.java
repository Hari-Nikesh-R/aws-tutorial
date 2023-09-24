package com.example.cognito;



import com.example.cognito.helper.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CognitoServiceImpl implements CognitoService {
    @Autowired
    private CognitoIdentityProviderClient cognitoUserPool;
    @Autowired
    CognitoConfig config;

    @Autowired
    Util util;


    @Override
    public ResponseEntity<ApiReturnResponse> changePassword(PasswordRequest changePasswordRequest, String accessToken) {
        try {
            ChangePasswordRequest passwordRequest = ChangePasswordRequest.builder().accessToken(accessToken.substring(7)).previousPassword(changePasswordRequest.getPreviousPassword()).proposedPassword(changePasswordRequest.getProposedPassword()).build();
            ChangePasswordResponse changePasswordResult = cognitoUserPool.changePassword(passwordRequest);
            return ResponseEntity.status(HttpStatus.OK.value()).body(util.setApiReturnResponse("Password changed successfully", true, HttpStatus.OK));
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(util.setApiReturnResponse("Password not changed", false, HttpStatus.FORBIDDEN));
        }
    }

    @Override
    public ResponseEntity<ApiReturnResponse> forgotPassword(String username) {
        try {
            ForgotPasswordRequest forgotPasswordRequest = ForgotPasswordRequest.builder().clientId(config.getClientId()).username(username).build();
            ForgotPasswordResponse forgotPasswordResult = cognitoUserPool.forgotPassword(forgotPasswordRequest);
            return ResponseEntity.status(HttpStatus.OK.value()).body(util.setApiReturnResponse("Requested Forgot password", true, HttpStatus.OK));

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(util.setApiReturnResponse("Failed to request Forgot password", false, HttpStatus.FORBIDDEN));
        }
    }

    @Override
    public ResponseEntity<ApiReturnResponse> signOutUser(String username) {
        try {
            cognitoUserPool.adminUserGlobalSignOut(AdminUserGlobalSignOutRequest.builder().userPoolId(config.getUserPoolId()).username(username).build());
            return ResponseEntity.status(HttpStatus.OK.value()).body(util.setApiReturnResponse("Sign out request successful", true, HttpStatus.OK));
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(util.setApiReturnResponse("Failed to sign out", false, HttpStatus.FORBIDDEN));
        }
    }

    @Override
    public ResponseEntity<ApiReturnResponse> confirmForgotPassword(AwsConfirmPasswordRequest confirmForgotPasswordRequest) {
        try {
            ConfirmForgotPasswordRequest confirmRequest = ConfirmForgotPasswordRequest.builder().clientId(config.getClientId()).username(confirmForgotPasswordRequest.getUsername()).password(confirmForgotPasswordRequest.getPassword()).confirmationCode(confirmForgotPasswordRequest.getConfirmationCode()).build();
            ConfirmForgotPasswordResponse confirmForgotPasswordResult = cognitoUserPool.confirmForgotPassword(confirmRequest);
            return ResponseEntity.status(HttpStatus.OK.value()).body(util.setApiReturnResponse("Code verified and password confirmed", true, HttpStatus.OK));

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(util.setApiReturnResponse("Invalid code and password not verified", false, HttpStatus.FORBIDDEN));
        }
    }

    @Override
    public Map<String, String> signIn(AuthDto authDto) {
        try {
            Map<String, String> authRequest = new LinkedHashMap<>() {{
                put("USERNAME", authDto.getUserName());
                put("PASSWORD", authDto.getPassword());
            }};
            AdminInitiateAuthRequest adminInitiateAuthRequest = AdminInitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                    .clientId(config.getClientId())
                    .userPoolId(config.getUserPoolId())
                    .authParameters(authRequest).build();
            AdminInitiateAuthResponse adminInitiateAuthResult = cognitoUserPool.adminInitiateAuth(adminInitiateAuthRequest);
            AuthenticationResultType authenticationResultType = adminInitiateAuthResult.authenticationResult();
            return new LinkedHashMap<>() {{
                put("idToken", authenticationResultType.idToken());
                put("accessToken", authenticationResultType.accessToken());
                put("refreshToken", authenticationResultType.refreshToken());
            }};

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return new LinkedHashMap<>() {{
                put("ERROR_DESC", exception.getMessage());
            }};
        }
    }
    @Override
    public Map<String, String> refreshToken(String token) {
        Map<String, String> authParameters = new LinkedHashMap<>() {{
            put("REFRESH_TOKEN", token.substring(7));
        }};
        AdminInitiateAuthRequest authRequest  = AdminInitiateAuthRequest.builder().userPoolId(config.getUserPoolId()).clientId(config.getClientId())
                .authParameters(authParameters).authFlow(AuthFlowType.REFRESH_TOKEN_AUTH).build();
        try {
            AdminInitiateAuthResponse adminInitiateAuthResult = cognitoUserPool.adminInitiateAuth(authRequest);
            AuthenticationResultType authenticationResultType = adminInitiateAuthResult.authenticationResult();
            String accessToken = authenticationResultType.accessToken();

            Integer expiresIn = authenticationResultType.expiresIn();
            System.out.println(expiresIn);
            return new LinkedHashMap<>() {{
                put("accessToken", authenticationResultType.accessToken());
            }};
        } catch (Exception e) {
            e.printStackTrace();
            return new LinkedHashMap<>() {{
                put("ERROR_DESC", e.getMessage());
            }};
        }
    }

    @Override
    public ResponseEntity<ApiReturnResponse> isAuthorized(String accessToken) {
        try {
            CognitoUserResponse cognitoUserResponse = util.getCognitoUserDetails(accessToken);
            return ResponseEntity.status(HttpStatus.OK.value()).body(util.setApiReturnResponse(cognitoUserResponse.getRole(), true, HttpStatus.OK));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(util.setApiReturnResponse(exception.getLocalizedMessage(), false, HttpStatus.FORBIDDEN));
        }
    }


//    @Override
//    public ResponseEntity<ApiReturnResponse> sendmail(EmailRequest emailRequest) {
//        try {
//            Destination destination = new Destination().withToAddresses(emailRequest.getTo());
//            Content content = new Content().withData(emailRequest.getBody());
//            Body body = new Body().withText(content);
//            Content subject = new Content().withData(emailRequest.getSubject());
//            Message message = new Message().withBody(body).withSubject(subject);
//            SendEmailRequest sendEmailRequest = new SendEmailRequest().withDestination(destination).withMessage(message).withSource(emailRequest.getFrom());
//            amazonSimpleEmailService.sendEmail(sendEmailRequest);
//            return ResponseEntity.status(HttpStatus.OK.value()).body(util.setApiReturnResponse("Email sent successfully", true, HttpStatus.OK));
//        } catch (Exception exception) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(util.setApiReturnResponse("Email not sent", false, HttpStatus.FORBIDDEN));
//        }
//    }

    @Override
    public ResponseEntity<ApiReturnResponse> deleteUser(String accessToken) {
        try {
            cognitoUserPool.deleteUser(DeleteUserRequest.builder().accessToken(accessToken).build());
            return ResponseEntity.status(HttpStatus.OK.value()).body(util.setApiReturnResponse("User Deleted successfully", true, HttpStatus.OK));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(util.setApiReturnResponse("User not deleted", false, HttpStatus.FORBIDDEN));
        }
    }

    @Override
    public ResponseEntity<ApiReturnResponse> disableUser(String userName) {
        try {
            AdminDisableUserResponse adminDisableUserResponse = cognitoUserPool.adminDisableUser(AdminDisableUserRequest.builder().userPoolId(config.getUserPoolId()).username(userName).build());
            return ResponseEntity.status(HttpStatus.OK.value()).body(util.setApiReturnResponse("User Disabled successfully", true, HttpStatus.OK));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(util.setApiReturnResponse("User not disabled", false, HttpStatus.FORBIDDEN));
        }

    }

    @Override
    public ResponseEntity<ApiReturnResponse> enableUser(String userName) {
        try {
            AdminEnableUserResponse adminEnableUserResponse = cognitoUserPool.adminEnableUser(AdminEnableUserRequest.builder().userPoolId(config.getUserPoolId()).username(userName).build());
            return ResponseEntity.status(HttpStatus.OK.value()).body(util.setApiReturnResponse("User Enabled successfully", true, HttpStatus.OK));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(util.setApiReturnResponse("User not enabled", false, HttpStatus.FORBIDDEN));
        }
    }

    @Override
    public ResponseEntity<ApiReturnResponse> updateUser(List<CognitoUserAttributeRequest> cognitoUserAttributeRequest, String accessToken) {
        try {
            List<AttributeType> attributeTypeList = new ArrayList<>();
            cognitoUserAttributeRequest.forEach((attributeType -> {
                attributeTypeList.add(AttributeType.builder().name(attributeType.getName()).value(attributeType.getValue()).build());
            }));
            UpdateUserAttributesResponse updateUserAttributesResponse = cognitoUserPool.updateUserAttributes(UpdateUserAttributesRequest.builder().accessToken(accessToken).userAttributes(attributeTypeList).build());
            return ResponseEntity.status(HttpStatus.OK.value()).body(util.setApiReturnResponse("Updated successfully", true, HttpStatus.OK));

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(util.setApiReturnResponse("Unable to update", false, HttpStatus.FORBIDDEN));
        }
    }
}


