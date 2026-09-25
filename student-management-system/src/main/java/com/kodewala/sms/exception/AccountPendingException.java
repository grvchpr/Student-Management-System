package com.kodewala.sms.exception;

public class AccountPendingException extends RuntimeException {

    public AccountPendingException(String message) {
        super(message);
    }
}
