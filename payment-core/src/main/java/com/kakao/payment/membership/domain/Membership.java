package com.kakao.payment.membership.domain;

import com.kakao.payment.common.BaseTimeEntity;
import com.kakao.payment.common.exception.IllegalParameterException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "memberships")
public class Membership extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uid;

    @Column(nullable = false)
    private String owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Name name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private int point;

    @Builder
    private Membership(Long id, String uid, String owner, Name name, Status status, int point) {
        validateParams(uid, owner, name, status, point);
        this.id = id;
        this.uid = uid;
        this.owner = owner;
        this.name = name;
        this.status = status;
        this.point = point;
    }

    private void validateParams(String uid, String owner, Name name, Status status, int point) {
        if (ObjectUtils.anyNull(uid, owner, name, status) || point < 0) {
            throw new IllegalParameterException();
        }
    }

    public void deactivate() {
        this.status = Status.N;
    }

    public void spend(int amount) {
        if (amount < 0) {
            throw new IllegalParameterException();
        }
        this.point += Math.round(amount * this.name.getRate());
    }
}