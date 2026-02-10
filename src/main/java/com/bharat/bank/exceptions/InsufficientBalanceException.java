package com.bharat.bank.exceptions;

public class InsufficientBalanceException extends RuntimeException{
    public InsufficientBalanceException(String error){
        super(error);
    }
}
