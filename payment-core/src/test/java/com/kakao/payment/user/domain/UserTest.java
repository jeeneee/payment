package com.kakao.payment.user.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.kakao.payment.common.exception.IllegalParameterException;
import java.util.Collections;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @BeforeEach
    void setUp() {
    }

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
}