package com.bharat.bank.audit_dashboard.services.impl;

import com.bharat.bank.account.dtos.AccountDTO;
import com.bharat.bank.account.repo.AccountRepository;
import com.bharat.bank.audit_dashboard.services.AuditorService;
import com.bharat.bank.auth_users.dtos.UserDTO;
import com.bharat.bank.auth_users.repo.UserRepository;
import com.bharat.bank.transactions.dtos.TransactionDTO;
import com.bharat.bank.transactions.repo.TransactionReposiitory;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditorServiceImpl implements AuditorService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionReposiitory transactionReposiitory;
    private final ModelMapper modelMapper;

    @Override
    public Map<String, Long> getSystemTotals() {
        long totalUsers = userRepository.count();
        long totalAccounts = accountRepository.count();
        long totalTransactions = transactionReposiitory.count();

        return Map.of(
                "totalUsers",totalUsers,
                "totalAccounts",totalAccounts,
                "totalTransactions",totalTransactions
        );
    }

    @Override
    public Optional<UserDTO> findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> modelMapper.map(user,UserDTO.class));
    }

    @Override
    public Optional<AccountDTO> findAccountDetailsByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(account -> modelMapper.map(account, AccountDTO.class));
    }

    @Override
    public List<TransactionDTO> findTransactionByAccountNumber(String accountNumber) {
        return transactionReposiitory.findByAccount_AccountNumber(accountNumber).stream()
                .map(transaction -> modelMapper.map(transaction,TransactionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TransactionDTO> findTransactionById(Long transactionId) {
        return transactionReposiitory.findById(transactionId)
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class));
    }
}
