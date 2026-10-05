package com.bankingsystem.exception;

public class AccountFrozenException extends BankingException {
    public AccountFrozenException(String message) {
        super(message);
    }
}
