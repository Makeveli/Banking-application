package com.bharat.bank.auth_users.services.util;

import com.bharat.bank.auth_users.repo.PasswordResetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class CodeGenerator {
    private final PasswordResetRepository passwordResetRepository;

    private static final String ALPHA_NUMERIC="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH=5;

    public String generateUniqueCode(){
        String code;
        do{
            code=generateRandomeCode();
        }while (passwordResetRepository.findByCode(code).isPresent());
        return code;
    }

    private String generateRandomeCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH); // String builder is used to create and keep addind character to a string without creating new reference.
        SecureRandom random = new SecureRandom();

        for(int i=0;i<CODE_LENGTH;i++){
            int index= random.nextInt(ALPHA_NUMERIC.length());
            sb.append(ALPHA_NUMERIC.charAt(index));
        }
        return sb.toString();
    }
}
