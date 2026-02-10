package com.bharat.bank.transactions.entity;

import com.bharat.bank.account.entity.Account;
import com.bharat.bank.enums.TransactionStatus;
import com.bharat.bank.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType transactionType;

    @Column(nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    private String description;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_id", nullable = false)
    private Account account;

    private String sourceAccount;
    private String destinationAccount;
}
