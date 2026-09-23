package com.kodewala.sms.exception;

public class DuplicateCourseCodeException extends RuntimeException {

    public DuplicateCourseCodeException(String message) {
        super(message);
    }
}