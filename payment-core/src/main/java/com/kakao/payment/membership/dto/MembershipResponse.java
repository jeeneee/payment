package com.kakao.payment.membership.dto;

import com.kakao.payment.membership.domain.Membership;
import com.kakao.payment.membership.domain.Status;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MembershipResponse {

    private int seq;

    private String membershipId;

    private String userId;

    private String membershipName;

    private LocalDateTime startDate;

    private Status membershipStatus;

    private int point;

    public static MembershipResponse from(int seq, Membership membership) {
        return MembershipResponse.builder()
            .seq(seq)
            .membershipId(membership.getUid())
            .userId(membership.getOwner().getUid())
            .membershipName(membership.getName().getValue())
            .startDate(membership.getCreatedDate())
            .membershipStatus(membership.getStatus())
            .point(membership.getPoint())
            .build();
    }
}
