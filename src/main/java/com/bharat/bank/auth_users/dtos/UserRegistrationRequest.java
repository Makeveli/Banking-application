package com.bharat.bank.auth_users.dtos;

import com.bharat.bank.role.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequest {
    @NotBlank(message = "FirstName is required")
    private String firstName;
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    private List<String> roles;

    private String phoneNumber;

    @NotBlank(message = "Password is required")
    private String password;
}
