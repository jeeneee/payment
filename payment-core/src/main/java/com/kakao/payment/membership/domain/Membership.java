package com.kakao.payment.membership.domain;

import static javax.persistence.FetchType.LAZY;

import com.kakao.payment.common.BaseTimeEntity;
import com.kakao.payment.common.exception.IllegalParameterException;
import com.kakao.payment.user.domain.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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

    @Column(nullable = false, unique = true)
    private String uid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Name name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private int point;

    @ManyToOne(fetch = LAZY)
    private User owner;

    @Builder
    private Membership(Long id, String uid, Name name, Status status, int point, User owner) {
        validateParams(uid, name, status, point, owner);
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.status = status;
        this.point = point;
        this.owner = owner;
    }

    private void validateParams(String uid, Name name, Status status, int point, User owner) {
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