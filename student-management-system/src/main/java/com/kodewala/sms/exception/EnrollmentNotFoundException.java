package com.kodewala.sms.exception;

public class EnrollmentNotFoundException
        extends RuntimeException {

    public EnrollmentNotFoundException(String message) {
        super(message);
    }
}