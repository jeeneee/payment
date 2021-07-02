package com.kakao.payment.exception;

public class ForbiddenException extends RuntimeException {

    private static final String MESSAGE = "Forbidden Error: ";

    public ForbiddenException(String message) {
        super(MESSAGE + message);
    }
}
