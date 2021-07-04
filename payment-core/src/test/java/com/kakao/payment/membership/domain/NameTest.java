package com.kakao.payment.membership.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.kakao.payment.common.exception.IllegalParameterException;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NameTest {

    @DisplayName("멤버십은 3가지로 나뉘고 적립비율은 결제금액의 1%이다.")
    @Test
    void enum_definition() {
        final float RATE = 0.01f;
        assertEquals(3, Name.values().length);
        Stream.of(Name.values())
            .forEach(name -> assertEquals(RATE, name.getRate()));
    }

    @DisplayName("멤버십에 속한 멤버십 이름으로 해당 멤버십을 찾을 수 있다.")
    @Test
    void find_ValueBelongsToName_Success() {
        Stream.of(Name.values())
            .forEach(name -> assertEquals(name, Name.find(name.getValue())));
    }

    @DisplayName("멤버십에 속하지 않은 멤버십 이름으로 찾으면 예외가 발생한다.")
    @Test
    void find_ValueNotBelongsToName_ExceptionThrown() {
        assertThrows(IllegalParameterException.class, () -> Name.find("kakao"));
    }
}