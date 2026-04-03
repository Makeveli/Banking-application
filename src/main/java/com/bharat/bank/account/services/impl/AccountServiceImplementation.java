package com.bharat.bank.account.services.impl;

import com.bharat.bank.account.dtos.AccountDTO;
import com.bharat.bank.account.entity.Account;
import com.bharat.bank.account.repo.AccountRepository;
import com.bharat.bank.account.services.AccountService;
import com.bharat.bank.auth_users.entity.User;
import com.bharat.bank.auth_users.services.UserService;
import com.bharat.bank.enums.AccountStatus;
import com.bharat.bank.enums.AccountType;
import com.bharat.bank.enums.Currency;
import com.bharat.bank.exceptions.BadRequestException;
import com.bharat.bank.exceptions.NotFoundException;
import com.bharat.bank.notification.dtos.NotificationDTO;
import com.bharat.bank.notification.services.NotificationService;
import com.bharat.bank.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccountServiceImplementation implements AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;

    private final Random random = new Random();


    @Override
    public Account createAccount(AccountType accountType, User user) {
        String accountNumber = generateUniqueAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountType(accountType)
                .currency(Currency.INR)
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        return accountRepository.save(account);
    }

    @Override
    public Response<List<AccountDTO>> getMyAccounts() {
        User user = userService.getCurrentLoggedInUser();
        List<AccountDTO> accountDTOList = accountRepository.findByUserId(user.getId())
                .stream()
                .map(account->modelMapper.map(account,AccountDTO.class))
                .toList();

        return Response.<List<AccountDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Accounts retreived successfully!")
                .data(accountDTOList)
                .build();
    }

    @Override
    public Response<?> closeAccount(String accountNumber) {
        User user = userService.getCurrentLoggedInUser();
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()->new NotFoundException("Account Not Found"));
        if(!user.getAccounts().contains(account)){
            throw new NotFoundException("Account does not belong to you.");
        }

        if(account.getBalance().compareTo(BigDecimal.ZERO) > 0)
            throw new BadRequestException("Account balance must be ZERO before closing");

        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());

        accountRepository.save(account);

        //Account close notification
        Map<String,Object> templateVariables = new HashMap<>();
        templateVariables.put("name",user.getFirstName());
        templateVariables.put("accountNumber",accountNumber);

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Account closed successfully")
                .templateName("account-closed")
                .templateVariables(templateVariables)
                .build();

        notificationService.sendMail(notificationDTO,user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account closed successfully")
                .build();
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do{
            accountNumber = "66"+(random.nextInt(90000000)+10000000);
        }while(accountRepository.findByAccountNumber(accountNumber).isPresent());
        return accountNumber;
    }

}
