package com.kakao.payment.common.exception;

import com.kakao.payment.exception.ForbiddenException;

public class PrincipalException extends ForbiddenException {

    public PrincipalException(String message) {
        super(message);
    }
}
