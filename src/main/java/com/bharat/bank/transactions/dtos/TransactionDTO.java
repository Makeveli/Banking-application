package com.bharat.bank.transactions.dtos;

import com.bharat.bank.account.dtos.AccountDTO;
import com.bharat.bank.account.entity.Account;
import com.bharat.bank.enums.TransactionStatus;
import com.bharat.bank.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private Long id;

    private BigDecimal amount;

    private TransactionType transactionType;

    private LocalDateTime transactionDate = LocalDateTime.now();

    private String description;

    private TransactionStatus status;

    @JsonBackReference
    private AccountDTO account;

    //for transfer
    private String sourceAccount;
    private String destinationAccount;

}
