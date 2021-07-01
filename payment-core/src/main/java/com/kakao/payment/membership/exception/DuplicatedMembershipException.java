package com.kakao.payment.membership.exception;

import com.kakao.payment.exception.DuplicatedException;

public class DuplicatedMembershipException extends DuplicatedException {

    public DuplicatedMembershipException(String message) {
        super(message);
    }
}
