package com.bankingsystem.exception;

public class InvalidTransactionException extends BankingException {
    public InvalidTransactionException(String message) {
        super(message);
    }
}
