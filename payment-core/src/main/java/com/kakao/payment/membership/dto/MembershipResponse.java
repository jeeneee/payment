package com.kakao.payment.membership.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private Long seq;

    @JsonProperty("membershipId")
    private String uid;

    @JsonProperty("userId")
    private String ownerUid;

    @JsonProperty("membershipName")
    private String name;

    private LocalDateTime startDate;

    @JsonProperty("membershipStatus")
    private Status status;

    private int point;

    public static MembershipResponse from(Membership membership) {
        return MembershipResponse.builder()
            .seq(membership.getId())
            .uid(membership.getUid())
            .ownerUid(membership.getOwnerUid())
            .name(membership.getName().getValue())
            .startDate(membership.getCreatedDate())
            .status(membership.getStatus())
            .point(membership.getPoint())
            .build();
    }
}
