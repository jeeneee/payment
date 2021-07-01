package com.kakao.payment.membership.dto;

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
public class MembershipUpdateRequest {

    @NotBlank(message = "membership_id must be provided")
    private String membershipId;

    @PositiveOrZero(message = "point must be provided and positive number.")
    private Integer amount;
}
