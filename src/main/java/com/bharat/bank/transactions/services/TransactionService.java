package com.bharat.bank.transactions.services;

import com.bharat.bank.response.Response;
import com.bharat.bank.transactions.dtos.TransactionDTO;
import com.bharat.bank.transactions.dtos.TransferRequestDTO;

import java.util.List;

public interface TransactionService {
    Response<?> createTransaction(TransferRequestDTO transferRequestDTO);
    Response<List<TransactionDTO>> getTransactionsForMyAccount(String accountNumber, int page, int size);
}
