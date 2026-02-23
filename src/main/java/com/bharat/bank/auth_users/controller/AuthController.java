package com.bharat.bank.auth_users.controller;

import com.bharat.bank.auth_users.dtos.LoginRequest;
import com.bharat.bank.auth_users.dtos.LoginResponse;
import com.bharat.bank.auth_users.dtos.PasswordResetRequest;
import com.bharat.bank.auth_users.dtos.UserRegistrationRequest;
import com.bharat.bank.auth_users.services.AuthService;
import com.bharat.bank.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response<String>> userRegister(@RequestBody @Valid UserRegistrationRequest userRegistrationRequest){
        return ResponseEntity.ok(authService.register(userRegistrationRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/forget-password")
    public ResponseEntity<Response<?>> forgetPassword(@RequestBody PasswordResetRequest passwordResetRequest){
        return ResponseEntity.ok(authService.forgetPassword(passwordResetRequest.getEmail()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Response<?>> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest){
        return ResponseEntity.ok(authService.updatePassword(passwordResetRequest));
    }

}
