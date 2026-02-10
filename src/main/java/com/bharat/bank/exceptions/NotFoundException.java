package com.bharat.bank.exceptions;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String error){
        super(error);
    }
}
