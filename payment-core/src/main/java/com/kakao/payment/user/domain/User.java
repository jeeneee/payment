package com.kakao.payment.user.domain;

import static javax.persistence.CascadeType.ALL;

import com.kakao.payment.common.domain.BaseTimeEntity;
import com.kakao.payment.common.exception.IllegalParameterException;
import com.kakao.payment.membership.domain.Membership;
import com.kakao.payment.membership.domain.Name;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uid;

    @OneToMany(mappedBy = "owner", cascade = ALL)
    private final List<Membership> memberships = new ArrayList<>();

    @Builder
    private User(Long id, String uid) {
        validateParams(uid);
        this.id = id;
        this.uid = uid;
    }

    private void validateParams(String uid) {
        if (StringUtils.isEmpty(uid)) {
            throw new IllegalParameterException();
        }
    }

    public boolean hasSameMembership(Name name) {
        return this.memberships.stream().anyMatch(membership -> membership.getName().equals(name));
    }
}
