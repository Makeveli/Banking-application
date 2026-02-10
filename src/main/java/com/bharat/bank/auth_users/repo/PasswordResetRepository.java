package com.bharat.bank.auth_users.repo;

import com.bharat.bank.auth_users.entity.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordResetCode,Long> {
    Optional<PasswordResetCode> findByCode(String code);
    void deleteByUserId(Long userId);
}
