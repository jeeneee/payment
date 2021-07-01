package com.kakao.payment.exception;

public class DuplicatedException extends RuntimeException {

    private static final String MESSAGE = "Duplicate Error: ";

    public DuplicatedException(String message) {
        super(MESSAGE + message);
    }
}

