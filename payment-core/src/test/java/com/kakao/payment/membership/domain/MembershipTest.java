package com.kakao.payment.membership.domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.kakao.payment.common.exception.IllegalParameterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MembershipTest {

    private Membership membership;

    @BeforeEach
    void setUp() {
        membership = Membership.builder()
            .uid("spc")
            .owner("test1")
            .name(Name.SPC)
            .status(Status.Y)
            .point(120)
            .build();
    }

    @DisplayName("멤버십 생성")
    @Test
    void builder_Satisfied_Success() {
        Membership created = Membership.builder()
            .uid("spc")
            .owner("test1")
            .name(Name.SPC)
            .status(Status.Y)
            .point(120)
            .build();

        assertAll(
            () -> assertEquals(membership.getUid(), created.getUid()),
            () -> assertEquals(membership.getName(), created.getName()),
            () -> assertEquals(membership.getStatus(), created.getStatus()),
            () -> assertEquals(membership.getPoint(), created.getPoint()),
            () -> assertEquals(membership.getOwner(), created.getOwner())
        );
    }

    @DisplayName("멤버십 생성 - 멤버십 아이디가 없으면 예외 발생")
    @Test
    void builder_WithoutUid_ExceptionThrown() {
        assertThrows(IllegalParameterException.class,
            () -> Membership.builder()
                .owner("test1")
                .name(Name.SPC)
                .status(Status.Y)
                .point(120)
                .build());
    }

    @DisplayName("멤버십 생성 - 멤버십 소유자가 없으면 예외 발생")
    @Test
    void builder_WithoutOwner_ExceptionThrown() {
        assertThrows(IllegalParameterException.class,
            () -> Membership.builder()
                .uid("spc")
                .name(Name.SPC)
                .status(Status.Y)
                .point(120)
                .build());
    }

    @DisplayName("멤버십 생성 - 멤버십 이름이 없으면 예외 발생")
    @Test
    void builder_WithoutName_ExceptionThrown() {
        assertThrows(IllegalParameterException.class,
            () -> Membership.builder()
                .uid("spc")
                .owner("test1")
                .status(Status.Y)
                .point(120)
                .build());
    }

    @DisplayName("멤버십 생성 - 멤버십 활성 상태가 없으면 예외 발생")
    @Test
    void builder_WithoutStatus_ExceptionThrown() {
        assertThrows(IllegalParameterException.class,
            () -> Membership.builder()
                .uid("spc")
                .owner("test1")
                .name(Name.SPC)
                .point(120)
                .build());
    }

    @DisplayName("멤버십 생성 - 멤버십 포인트가 음수이면 예외 발생")
    @Test
    void builder_PointLessThanZero_ExceptionThrown() {
        assertThrows(IllegalParameterException.class,
            () -> Membership.builder()
                .uid("spc")
                .owner("test1")
                .name(Name.SPC)
                .status(Status.Y)
                .point(-120)
                .build());
    }

    @DisplayName("멤버십 비활성화")
    @Test
    void deactivate_Satisfied_Success() {
        assertEquals(Status.Y, membership.getStatus());

        membership.deactivate();

        assertEquals(Status.N, membership.getStatus());
    }

    @DisplayName("멤버십 포인트 적립 - 반올림(버림)")
    @Test
    void spend_AmountToFloor_Success() {
        assertEquals(120, membership.getPoint());

        membership.spend(8049);

        assertEquals(200, membership.getPoint());
    }

    @DisplayName("멤버십 포인트 적립 - 반올림(올림)")
    @Test
    void spend_AmountToCeil_Success() {
        assertEquals(120, membership.getPoint());

        membership.spend(8050);

        assertEquals(201, membership.getPoint());
    }

    @DisplayName("멤버십 포인트 적립 - 소비 금액이 음수이면 예외 발생")
    @Test
    void spend_AmountLessThanZero_Success() {
        assertEquals(120, membership.getPoint());

        assertThrows(IllegalParameterException.class, () -> membership.spend(-8000));
    }
}