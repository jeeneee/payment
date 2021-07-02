package com.kakao.payment.membership.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakao.payment.membership.domain.Membership;
import com.kakao.payment.membership.domain.Name;
import com.kakao.payment.membership.domain.Status;
import com.kakao.payment.user.domain.User;
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
    @JsonProperty("membershipId")
    private String uid;

    @NotBlank(message = "membership_id must be provided")
    @JsonProperty("membershipName")
    private String name;

    @PositiveOrZero(message = "point must be provided and positive number.")
    private Integer point;

    public Membership toEntity(User owner) {
        return Membership.builder()
            .uid(uid)
            .owner(owner)
            .name(getName())
            .status(Status.Y)
            .point(point)
            .build();
    }

    public Name getName() {
        return Name.find(this.name);
    }
}
