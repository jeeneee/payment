package com.kakao.payment.membership.exception;

import com.kakao.payment.exception.NotFoundException;

public class MembershipNotFoundException extends NotFoundException {

    public MembershipNotFoundException(String message) {
        super(message);
    }
}
