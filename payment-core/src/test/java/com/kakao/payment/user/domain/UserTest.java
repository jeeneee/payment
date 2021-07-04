package com.kakao.payment.user.domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kakao.payment.common.exception.IllegalParameterException;
import com.kakao.payment.membership.domain.Membership;
import com.kakao.payment.membership.domain.Name;
import com.kakao.payment.membership.domain.Status;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @DisplayName("유저 생성")
    @Test
    void builder_User_Success() {
        User created = User.builder()
            .uid("test1")
            .build();

        assertAll(
            () -> assertEquals("test1", created.getUid()),
            () -> assertEquals(Collections.emptyList(), created.getMemberships())
        );
    }

    @DisplayName("유저 생성 - 유저 아이디가 없으면 예외 발생")
    @Test
    void builder_WithoutUid_ExceptionThrown() {
        assertThrows(IllegalParameterException.class, () -> User.builder().build());
    }

    @DisplayName("같은 이름의 멤버십 소유 여부 확인")
    @Test
    void hasSameMembership_ExistsNoExists_TrueFalse() {
        User owner = User.builder()
            .uid("test1")
            .build();
        Membership membership = Membership.builder()
            .id(1L)
            .uid("cj")
            .name(Name.CJ)
            .status(Status.Y)
            .point(5210)
            .owner(owner)
            .build();

        owner.getMemberships().add(membership);
        assertTrue(owner.hasSameMembership(Name.CJ));

        owner.getMemberships().remove(membership);
        assertFalse(owner.hasSameMembership(Name.CJ));


    }
}