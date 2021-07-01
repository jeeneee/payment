package com.kakao.payment.common.exception;

import com.kakao.payment.exception.BadRequestException;

public class IllegalParameterException extends BadRequestException {

    private static final String MESSAGE = "Invalid Parameter";

    public IllegalParameterException() {
        super(MESSAGE);
    }
}
