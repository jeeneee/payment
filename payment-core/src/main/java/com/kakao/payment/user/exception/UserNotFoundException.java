package com.kakao.payment.user.exception;

import com.kakao.payment.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
