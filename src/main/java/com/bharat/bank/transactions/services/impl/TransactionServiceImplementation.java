package com.bharat.bank.transactions.services.impl;

import com.bharat.bank.account.entity.Account;
import com.bharat.bank.account.repo.AccountRepository;
import com.bharat.bank.account.services.AccountService;
import com.bharat.bank.auth_users.entity.User;
import com.bharat.bank.auth_users.services.UserService;
import com.bharat.bank.enums.TransactionStatus;
import com.bharat.bank.enums.TransactionType;
import com.bharat.bank.exceptions.BadRequestException;
import com.bharat.bank.exceptions.InvalidTransactionException;
import com.bharat.bank.exceptions.NotFoundException;
import com.bharat.bank.notification.dtos.NotificationDTO;
import com.bharat.bank.notification.services.NotificationService;
import com.bharat.bank.response.Response;
import com.bharat.bank.transactions.dtos.TransactionDTO;
import com.bharat.bank.transactions.dtos.TransferRequestDTO;
import com.bharat.bank.transactions.entity.Transaction;
import com.bharat.bank.transactions.repo.TransactionReposiitory;
import com.bharat.bank.transactions.services.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImplementation implements TransactionService {

    private final UserService userService;
    private final AccountService accountService;
    private final TransactionReposiitory transactionReposiitory;
    private final NotificationService notificationService;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public Response<?> createTransaction(TransferRequestDTO transferRequestDTO) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(transferRequestDTO.getTransactionType());
        transaction.setAmount(transferRequestDTO.getAmount());
        transaction.setDescription(transferRequestDTO.getDescription());

        switch (transferRequestDTO.getTransactionType()){
            case DEPOSIT -> handleDeposit(transferRequestDTO,transaction);
            case WITHDRAWL -> hadleWithdrawl(transferRequestDTO,transaction);
            case TRANSFER -> handleTransfer(transferRequestDTO,transaction);
            default -> throw new InvalidTransactionException("Invalid Transaction Type");
        }

        transaction.setStatus(TransactionStatus.SUCCESS);
        Transaction savedTransaction = transactionReposiitory.save(transaction);

        //send notification
        sendTransactionNotification(savedTransaction);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Transaction successful.")
                .build();
    }

    private void sendTransactionNotification(Transaction savedTransaction) {
        User user = savedTransaction.getAccount().getUser();
        String subject;
        String template;

        Map<String, Object> templateVariables = new HashMap<>();

        templateVariables.put("name",user.getFirstName());
        templateVariables.put("amount",savedTransaction.getAmount());
        templateVariables.put("accountNumber",savedTransaction.getAccount().getAccountNumber());
        templateVariables.put("date",savedTransaction.getTransactionDate());
        templateVariables.put("balance",savedTransaction.getAccount().getBalance());

        if(savedTransaction.getTransactionType() == TransactionType.DEPOSIT) {
            subject = "Credit Alert";
            template = "Credit-alert";

            NotificationDTO notificationEmailToSendOut = NotificationDTO.builder()
                    .recipient(user.getEmail())
                    .subject(subject)
                    .templateName(template)
                    .templateVariables(templateVariables)
                    .build();
            notificationService.sendMail(notificationEmailToSendOut,user);
        }
        else if(savedTransaction.getTransactionType() == TransactionType.WITHDRAWL){
            subject = "Debit Alert";
            template = "Debit-alert";

            NotificationDTO notificationEmailToSendOut = NotificationDTO.builder()
                    .recipient(user.getEmail())
                    .subject(subject)
                    .templateName(template)
                    .templateVariables(templateVariables)
                    .build();
            notificationService.sendMail(notificationEmailToSendOut,user);
        }
        else if(savedTransaction.getTransactionType()==TransactionType.TRANSFER){
            subject = "Debit Alert";
            template = "Debit-alert";

            NotificationDTO notificationEmailToSendOut = NotificationDTO.builder()
                    .recipient(user.getEmail())
                    .subject(subject)
                    .templateName(template)
                    .templateVariables(templateVariables)
                    .build();
            notificationService.sendMail(notificationEmailToSendOut,user);

            //Send notification to the receiver
            Account destination = accountRepository.findByAccountNumber(savedTransaction.getDestinationAccount())
                    .orElseThrow(()-> new NotFoundException("Destination account not found!"));

            User receiver = destination.getUser();

            Map<String, Object> receiverVariables = new HashMap<>();

            receiverVariables.put("name",receiver.getFirstName());
            receiverVariables.put("amount",savedTransaction.getAmount());
            receiverVariables.put("accountNumber",savedTransaction.getDestinationAccount());
            receiverVariables.put("date",savedTransaction.getTransactionDate());
            receiverVariables .put("balance",destination.getBalance());

            NotificationDTO notificationEmailToReceiver = NotificationDTO.builder()
                    .recipient(receiver.getEmail())
                    .subject("Credit Alert")
                    .templateName("credit-alert")
                    .templateVariables(receiverVariables)
                    .build();
            notificationService.sendMail(notificationEmailToReceiver,receiver);

        }

    }

    private void handleTransfer(TransferRequestDTO transferRequestDTO, Transaction transaction) {
        Account senderAccount = accountRepository.findByAccountNumber(transferRequestDTO.getAccountNumber())
                .orElseThrow(()-> new NotFoundException("Sender account not found!"));
        Account destinationAccount = accountRepository.findByAccountNumber(transferRequestDTO.getDestinationAccountNumber())
                .orElseThrow(()-> new NotFoundException("Destination account does not exists"));

        if(senderAccount.getBalance().compareTo(transferRequestDTO.getAmount())<0)
            throw new InvalidTransactionException("Insufficient Balance!");

        senderAccount.setBalance(senderAccount.getBalance().subtract(transferRequestDTO.getAmount()));
        accountRepository.save(senderAccount);

        destinationAccount.setBalance(destinationAccount.getBalance().add(transferRequestDTO.getAmount()));
        accountRepository.save(destinationAccount);
        transaction.setAccount(senderAccount);
        transaction.setSourceAccount(senderAccount.getAccountNumber());
        transaction.setDestinationAccount(destinationAccount.getAccountNumber());

    }

    private void hadleWithdrawl(TransferRequestDTO transferRequestDTO, Transaction transaction) {
        Account account = accountRepository.findByAccountNumber(transferRequestDTO.getAccountNumber())
                .orElseThrow(()-> new NotFoundException("Account not found"));
        if(transferRequestDTO.getAmount().compareTo(account.getBalance())>0)
            throw new InvalidTransactionException("Amount insufficient!");

        account.setBalance(account.getBalance().subtract(transferRequestDTO.getAmount()));
        transaction.setAccount(account);
        accountRepository.save(account);
    }

    private void handleDeposit(TransferRequestDTO transferRequestDTO, Transaction transaction) {
        Account account = accountRepository.findByAccountNumber(transferRequestDTO.getAccountNumber())
                .orElseThrow(()-> new NotFoundException("Account not found"));
        account.setBalance(account.getBalance().add(transferRequestDTO.getAmount()));
        transaction.setAccount(account);
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public Response<List<TransactionDTO>> getTransactionsForMyAccount(String accountNumber, int page, int size) {
        User user = userService.getCurrentLoggedInUser();
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new NotFoundException("Account not found!"));

        if(!account.getUser().getId().equals(user.getId())){
            throw new BadRequestException("Account does not belong to the authenticated user");
        }

        Pageable pageable = PageRequest.of(page,size, Sort.by("transactionDate").descending());
        Page<Transaction> transactions = transactionReposiitory.findByAccount_AccountNumber(accountNumber,pageable);

        List<TransactionDTO> transactionDTOList = transactions.stream()
                .map(transaction -> modelMapper.map(transaction,TransactionDTO.class))
                .toList();

        return  Response.<List<TransactionDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Transactions fetched successfully.")
                .data(transactionDTOList)
                .meta(Map.of(
                        "currentPage",transactions.getNumber(),
                        "totalItems",transactions.getTotalElements(),
                        "totalElement",transactions.getTotalElements(),
                        "totalPages",transactions.getTotalPages()
                ))
                .build();
    }
}
