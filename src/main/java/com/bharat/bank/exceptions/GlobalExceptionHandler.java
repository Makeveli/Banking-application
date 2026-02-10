package com.bharat.bank.exceptions;

import com.bharat.bank.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.service.annotation.HttpExchange;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<?>> handleAllUnknownExceptions(Exception e){
        Response<?> response = Response.builder().
                statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response<?>> handleNotFoundExceptions(NotFoundException e){
        Response<?> response = Response.builder().
                statusCode(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<Response<?>> handleInvalidTransactionExceptions(InvalidTransactionException e){
        Response<?> response = Response.builder().
                statusCode(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Response<?>> handleInsufficientBalanecExceptions(InsufficientBalanceException e){
        Response<?> response = Response.builder().
                statusCode(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Response<?>> handleBadRequestExceptions(BadRequestException e){
        Response<?> response = Response.builder().
                statusCode(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }


}
