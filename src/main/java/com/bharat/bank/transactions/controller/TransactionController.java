package com.bharat.bank.transactions.controller;

import com.bharat.bank.response.Response;
import com.bharat.bank.transactions.dtos.TransactionDTO;
import com.bharat.bank.transactions.dtos.TransferRequestDTO;
import com.bharat.bank.transactions.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/create-transaction")
    public ResponseEntity<Response<?>> createTransactions(@RequestBody @Valid TransferRequestDTO transferRequestDTO){
        return ResponseEntity.ok(transactionService.createTransaction(transferRequestDTO));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Response<List<TransactionDTO>>> getTransactions(@PathVariable String accountNumber,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "50") int size){
        return ResponseEntity.ok(transactionService.getTransactionsForMyAccount(accountNumber,page,size));
    }

}
