package com.bharat.bank.audit_dashboard.services;

import com.bharat.bank.account.dtos.AccountDTO;
import com.bharat.bank.auth_users.dtos.UserDTO;
import com.bharat.bank.transactions.dtos.TransactionDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AuditorService {
    Map<String, Long> getSystemTotals();

    Optional<UserDTO> findUserByEmail(String email);

    Optional<AccountDTO> findAccountDetailsByAccountNumber(String accountNumber);

    List<TransactionDTO> findTransactionByAccountNumber(String accountNumber);

    Optional<TransactionDTO> findTransactionById(Long transactionId);
}
