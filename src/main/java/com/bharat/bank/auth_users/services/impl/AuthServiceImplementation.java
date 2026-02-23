package com.bharat.bank.auth_users.services.impl;

import com.bharat.bank.account.entity.Account;
import com.bharat.bank.account.services.AccountService;
import com.bharat.bank.auth_users.dtos.LoginRequest;
import com.bharat.bank.auth_users.dtos.LoginResponse;
import com.bharat.bank.auth_users.dtos.PasswordResetRequest;
import com.bharat.bank.auth_users.dtos.UserRegistrationRequest;
import com.bharat.bank.auth_users.entity.PasswordResetCode;
import com.bharat.bank.auth_users.entity.User;
import com.bharat.bank.auth_users.repo.PasswordResetRepository;
import com.bharat.bank.auth_users.repo.UserRepository;
import com.bharat.bank.auth_users.services.AuthService;
import com.bharat.bank.auth_users.services.util.CodeGenerator;
import com.bharat.bank.enums.AccountType;
import com.bharat.bank.enums.Currency;
import com.bharat.bank.exceptions.BadRequestException;
import com.bharat.bank.exceptions.NotFoundException;
import com.bharat.bank.notification.dtos.NotificationDTO;
import com.bharat.bank.notification.services.NotificationService;
import com.bharat.bank.response.Response;
import com.bharat.bank.role.entity.Role;
import com.bharat.bank.role.repo.RoleRepository;
import com.bharat.bank.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImplementation implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final NotificationService notificationService;
    private final CodeGenerator codeGenerator;
    private final PasswordResetRepository passwordResetRepository;
    private final AccountService accountService;

    @Value("${password.reset.link}")
    private String resetLink;

    @Override
    public Response<String> register(UserRegistrationRequest request) {
        List<Role> roles;
        if(request.getRoles() == null || request.getRoles().isEmpty()){
            //DEFAULT TO CUSTOMER
            Role defaultRole = roleRepository.findByName("CUSTOMER")
                    .orElseThrow(()->new NotFoundException("Customer role not found,Contact admin"));
            roles= Collections.singletonList(defaultRole);
        }
        else{
            roles = request.getRoles().stream()
                    .map(roleName->roleRepository.findByName(roleName)
                            .orElseThrow(()-> new NotFoundException("ROLE NOT FOUND "+ roleName)))
                    .collect(Collectors.toList());
        }

        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new BadRequestException("Email Already Present");
        }
        User user  = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .roles(roles)
                .active(true)
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        //TODO AUTO GENERATE AN ACCOUNT NUMBER FOR THE USER
        Account savedAccount  = accountService.createAccount(AccountType.SAVINGS, savedUser);

        //SEND A WELCOME EMAIL
        Map<String, Object> vars = new HashMap<>();
        vars.put("name",savedUser.getFirstName());

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(savedUser.getEmail())
                .subject("Welcome to Bharat Bank.🎉")
                .templateName("welcome")
                .templateVariables(vars)
                .build();

        notificationService.sendMail(notificationDTO, savedUser);

        //SEND ACCOUNT CREATION/DETAILS EMAIL
        Map<String,Object> accountVars = new HashMap<>();
        accountVars.put("name",savedUser.getFirstName());
        accountVars.put("accountNumber",savedAccount.getAccountNumber());
        accountVars.put("accountType", AccountType.SAVINGS.name());
        accountVars.put("currency", Currency.INR);

        NotificationDTO accountCreationEmail = NotificationDTO.builder()
                .recipient(savedUser.getEmail())
                .subject("Your New Bank Account Has been created.✅")
                .templateName("account-created")
                .templateVariables(accountVars)
                .build();
        notificationService.sendMail(accountCreationEmail,savedUser);

        return Response.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Your account has been created successfully")
                .data("Email of your account details has been sent to your registered email. Your account number is: "+savedAccount.getAccountNumber())
                .build();



    }

    @Override
    public Response<LoginResponse> login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();;
        String password = loginRequest.getPassword();

        User user = userRepository.findByEmail(email).orElseThrow(()-> new NotFoundException(String.format("User with email %s not found!",email)));

        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new BadRequestException("Email id and password does not match!");
        }

        String token = tokenService.generateToken(user.getEmail());
        LoginResponse loginResponse = LoginResponse.builder()
                .token(token)
                .roles(user.getRoles().stream()
                        .map(Role::getName).toList())
                .build();
        return Response.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login successful")
                .data(loginResponse)
                .build();

    }

    @Override
    @Transactional
    public Response<?> forgetPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new NotFoundException("User not found."));
        passwordResetRepository.deleteByUserId(user.getId());
        String code = codeGenerator.generateUniqueCode();

        PasswordResetCode passwordResetCode = PasswordResetCode.builder()
                .code(code)
                .user(user)
                .expiryDate(calculateExpiryDate())
                .used(false)
                .build();
        passwordResetRepository.save(passwordResetCode);

        //send email reset link out
        Map<String,Object> resetEmailVargs= new HashMap<>();
        resetEmailVargs.put("name",user.getFirstName());
        resetEmailVargs.put("resetLink",resetLink + code);

        NotificationDTO resetEmail = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Password Reset Link.")
                .templateName("password-reset")
                .templateVariables(resetEmailVargs)
                .build();
        notificationService.sendMail(resetEmail,user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password reset code sent to your email")
                .build();
    }

    private LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusMinutes(30);
    }

    @Override
    @Transactional
    public Response<?> updatePassword(PasswordResetRequest passwordResetRequest) {
        String code = passwordResetRequest.getCode();
        String newPassword = passwordResetRequest.getNewPassword();

        PasswordResetCode passwordResetCode = passwordResetRepository.findByCode(code)
                .orElseThrow(() -> new BadRequestException("Invalid Reset Code."));
        if(passwordResetCode.getExpiryDate().isBefore(LocalDateTime.now())){
            passwordResetRepository.delete(passwordResetCode);
            throw new BadRequestException("Reset code has expired.");
        }

        User user = passwordResetCode.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetRepository.delete(passwordResetCode);

        //Send confirmation email
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name",user.getFirstName());

        NotificationDTO confirmationEmail = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Password Updated successfully.")
                .templateName("password-updated-confirmation")
                .templateVariables(templateVariables)
                .build();

        notificationService.sendMail(confirmationEmail,user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password Reset Successfully")
                .build();

    }
}
