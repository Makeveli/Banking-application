package com.bharat.bank.auth_users.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PasswordResetRequest {
    private String email;
    private String code;
    private String newPassword;
}
