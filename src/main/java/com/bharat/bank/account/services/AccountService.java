package com.bharat.bank.account.services;

import com.bharat.bank.account.dtos.AccountDTO;
import com.bharat.bank.account.entity.Account;
import com.bharat.bank.auth_users.entity.User;
import com.bharat.bank.enums.AccountType;
import com.bharat.bank.response.Response;

import java.util.List;

public interface AccountService {
    Account createAccount(AccountType accountType, User user);
    Response<List<AccountDTO>> getMyAccounts();
    Response<?> closeAccount(String accountNumber);
}
