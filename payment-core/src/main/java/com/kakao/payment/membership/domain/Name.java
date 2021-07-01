package com.kakao.payment.membership.domain;

import com.kakao.payment.common.exception.IllegalParameterException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum Name {

    SPC("happypoint", 0.01f),
    SSG("shinsegaepoint", 0.01f),
    CJ("cjone", 0.01f);

    private static final Map<String, Name> map = Collections.unmodifiableMap(Stream.of(values())
        .collect(Collectors.toMap(Name::getValue, Function.identity())));
    private final String value;
    private final float rate;

    Name(String value, float rate) {
        this.value = value;
        this.rate = rate;
    }

    public static Name find(String value) {
        return Optional.ofNullable(map.get(value)).orElseThrow(IllegalParameterException::new);
    }
}
