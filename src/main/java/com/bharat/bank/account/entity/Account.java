package com.bharat.bank.account.entity;

import com.bharat.bank.auth_users.entity.User;
import com.bharat.bank.enums.AccountStatus;
import com.bharat.bank.enums.AccountType;
import com.bharat.bank.enums.Currency;
import com.bharat.bank.transactions.entity.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="accounts")
@Builder
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true,length = 15)
    private String accountNumber;

    @Column(nullable = false, precision = 19,scale = 2)
    private BigDecimal balance=BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @OneToMany(mappedBy = "account",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    List<Transaction> transactions = new ArrayList<>();

    private LocalDateTime closedAt;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

}
