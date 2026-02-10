package com.bharat.bank.transactions.dtos;

import com.bharat.bank.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferRequestDTO {
    private TransactionType transactionType;
    private BigDecimal amount;
    private String accountNumber;
    private String description;

    private String destinationAccountNumber;
}
