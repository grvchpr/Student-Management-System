package com.kodewala.sms.exception;

public class AccountRejectedException extends RuntimeException {

    public AccountRejectedException(String message) {
        super(message);
    }
}
