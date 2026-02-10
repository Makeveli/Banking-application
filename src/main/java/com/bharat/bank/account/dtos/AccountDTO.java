package com.bharat.bank.account.dtos;

import com.bharat.bank.auth_users.dtos.UserDTO;
import com.bharat.bank.auth_users.entity.User;
import com.bharat.bank.enums.AccountStatus;
import com.bharat.bank.enums.AccountType;
import com.bharat.bank.enums.Currency;
import com.bharat.bank.transactions.dtos.TransactionDTO;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
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
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDTO {

    private Long id;

    private String accountNumber;

    private BigDecimal balance;

    private AccountType accountType;

    @JsonBackReference // this will not be added to the account DTO. it will be ignored because it is a back reference
    private UserDTO user;

    private Currency currency;

    private AccountStatus status;

    @JsonManagedReference // it helps avoid recursion loop by ignoring the AccountDTO within the transactionDTO
    List<TransactionDTO> transactions;

    private LocalDateTime closedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
