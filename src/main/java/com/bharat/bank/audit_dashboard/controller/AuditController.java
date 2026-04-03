package com.bharat.bank.audit_dashboard.controller;

import com.bharat.bank.account.dtos.AccountDTO;
import com.bharat.bank.audit_dashboard.services.AuditorService;
import com.bharat.bank.auth_users.dtos.UserDTO;
import com.bharat.bank.transactions.dtos.TransactionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/audit")
//@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('AUDITOR')")
public class AuditController {

    private final AuditorService auditorService;

    @GetMapping("/totals")
    public ResponseEntity<Map<String,Long>> getSystemTotals(){
        return ResponseEntity.ok(auditorService.getSystemTotals());
    }

    @GetMapping("/users")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email){
        Optional<UserDTO> userDTO = auditorService.findUserByEmail(email);
        return userDTO.map(ResponseEntity::ok)
                .orElseGet(()->ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/accounts")
    public ResponseEntity<AccountDTO> getAccountDetailsByAccountNumber(@RequestParam String accountNumber){
        Optional<AccountDTO> accountDTO = auditorService.findAccountDetailsByAccountNumber(accountNumber);
        return accountDTO.map(ResponseEntity::ok)
                .orElseGet(()->ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/transactions/by-account")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByAccountNumber(@RequestParam String accountNumber){
        List<TransactionDTO> transactionDTOList = auditorService.findTransactionByAccountNumber(accountNumber);

        if(transactionDTOList.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(transactionDTOList);
    }

    @GetMapping("/transactions/by-id")
    public ResponseEntity<TransactionDTO> getTransactionById(@RequestParam Long id){
        Optional<TransactionDTO> transactionDTO = auditorService.findTransactionById(id);

        return transactionDTO.map(ResponseEntity::ok)
                .orElseGet(()-> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

}
