package com.bharat.bank.auth_users.services;

import com.bharat.bank.auth_users.dtos.LoginRequest;
import com.bharat.bank.auth_users.dtos.LoginResponse;
import com.bharat.bank.auth_users.dtos.PasswordResetRequest;
import com.bharat.bank.auth_users.dtos.UserRegistrationRequest;
import com.bharat.bank.response.Response;

public interface AuthService {
    Response<String> register(UserRegistrationRequest request);
    Response<LoginResponse> login(LoginRequest loginRequest);
    Response<?> forgetPassword(String email);
    Response<?> updatePassword(PasswordResetRequest passwordResetRequest);
}
