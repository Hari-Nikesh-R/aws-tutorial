package com.example.cognito;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *   add role_name and mobile_number in the format of [list of user attribute]\
 *   Check inside the super class (SignUpRequest) for List<UserAttribute> template.
 */

@Builder
@Data
public class RegistrationRequest implements Serializable {
    private String username;
    private String name;
    private String schemeName;
    private List<CognitoUserAttributeRequest> userAttributes;
}
