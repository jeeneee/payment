package com.kakao.payment.membership.dto;

import com.kakao.payment.membership.domain.Membership;
import com.kakao.payment.membership.domain.Name;
import com.kakao.payment.membership.domain.Status;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MembershipSaveRequest {

    @NotBlank(message = "membership_id must be provided")
    private String membershipId;

    @NotBlank(message = "membership_id must be provided")
    private String membershipName;

    @PositiveOrZero(message = "point must be provided and positive number.")
    private Integer point;

    public Membership toEntity(String owner) {
        return Membership.builder()
            .uid(membershipId)
            .owner(owner)
            .name(Name.find(membershipName))
            .status(Status.Y)
            .point(point)
            .build();
    }
}
