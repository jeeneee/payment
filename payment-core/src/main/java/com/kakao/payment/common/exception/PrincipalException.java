package com.kakao.payment.common.exception;

import com.kakao.payment.exception.BadRequestException;

public class PrincipalException extends BadRequestException {

    public PrincipalException(String message) {
        super(message);
    }
}
