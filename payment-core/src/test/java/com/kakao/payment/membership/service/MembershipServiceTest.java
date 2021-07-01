package com.kakao.payment.membership.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import com.kakao.payment.membership.domain.Membership;
import com.kakao.payment.membership.domain.MembershipRepository;
import com.kakao.payment.membership.domain.Name;
import com.kakao.payment.membership.domain.Status;
import com.kakao.payment.membership.dto.MembershipResponse;
import com.kakao.payment.membership.dto.MembershipSaveRequest;
import com.kakao.payment.membership.dto.MembershipUpdateRequest;
import com.kakao.payment.membership.exception.DuplicatedMembershipException;
import com.kakao.payment.membership.exception.MembershipNotFoundException;
import java.util.List;
import java.util.Optional;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {

    private MembershipService membershipService;

    @Mock
    private MembershipRepository membershipRepository;

    private Membership membership;

    @BeforeEach
    void setUp() {
        membershipService = new MembershipService(membershipRepository);
        membership = Membership.builder()
            .id(1L)
            .uid("cj")
            .owner("test1")
            .name(Name.CJ)
            .status(Status.Y)
            .point(5210)
            .build();
    }

    @DisplayName("멤버십 전체 조회")
    @Test
    void findAll_MembershipsExist_Success() {
        // given
        List<Membership> memberships = Lists.list(membership);
        given(membershipRepository.findAll()).willReturn(memberships);

        // when
        List<MembershipResponse> responses = membershipService.findAll();

        // then
        MembershipResponse response = responses.get(0);
        assertAll(
            () -> assertEquals(1, response.getSeq()),
            () -> assertEquals(membership.getUid(), response.getMembershipId()),
            () -> assertEquals(membership.getOwner(), response.getUserId()),
            () -> assertEquals(membership.getName().getValue(), response.getMembershipName()),
            () -> assertEquals(membership.getStatus(), response.getMembershipStatus()),
            () -> assertEquals(membership.getPoint(), response.getPoint())
        );
    }

    @DisplayName("멤버십 등록하기")
    @Test
    void save_Membership_Success() {
        // given
        final String owner = "test1";
        MembershipSaveRequest request = MembershipSaveRequest.builder()
            .membershipId("cj")
            .membershipName("cjone")
            .point(5210)
            .build();
        given(membershipRepository.save(any(Membership.class))).willReturn(membership);

        // when
        MembershipResponse response = membershipService.save(request, owner);

        // then
        assertAll(
            () -> assertEquals(1, response.getSeq()),
            () -> assertEquals(membership.getUid(), response.getMembershipId()),
            () -> assertEquals(membership.getOwner(), response.getUserId()),
            () -> assertEquals(membership.getName().getValue(), response.getMembershipName()),
            () -> assertEquals(membership.getStatus(), response.getMembershipStatus()),
            () -> assertEquals(membership.getPoint(), response.getPoint())
        );
    }

    @DisplayName("멤버십 등록하기 - 해당 멤버십이 이미 존재하면 예외가 발생한다.")
    @Test
    void save_MembershipAlreadyExists_ExceptionThrown() {
        final String owner = "test1";
        MembershipSaveRequest request = MembershipSaveRequest.builder()
            .membershipId("cj")
            .membershipName("cjone")
            .point(5210)
            .build();
        given(membershipRepository.findByUid(request.getMembershipId()))
            .willReturn(Optional.of(membership));

        assertThrows(DuplicatedMembershipException.class,
            () -> membershipService.save(request, owner));
    }

    @DisplayName("멤버십 삭제(비활성화)")
    @Test
    void deactivate_MembershipExists_Success() {
        // given
        assertEquals(Status.Y, membership.getStatus());
        given(membershipRepository.findByUid(membership.getUid()))
            .willReturn(Optional.of(membership));

        // when
        boolean success = membershipService.deactivate(membership.getUid());

        // then
        assertTrue(success);
        assertEquals(Status.N, membership.getStatus());
    }

    @DisplayName("멤버십 상세 조회")
    @Test
    void findOne_MembershipExists_Success() {
        // given
        given(membershipRepository.findByUid(membership.getUid()))
            .willReturn(Optional.of(membership));

        // when
        MembershipResponse response = membershipService.findOne(membership.getUid());

        // then
        assertAll(
            () -> assertEquals(1, response.getSeq()),
            () -> assertEquals(membership.getUid(), response.getMembershipId()),
            () -> assertEquals(membership.getOwner(), response.getUserId()),
            () -> assertEquals(membership.getName().getValue(), response.getMembershipName()),
            () -> assertEquals(membership.getStatus(), response.getMembershipStatus()),
            () -> assertEquals(membership.getPoint(), response.getPoint())
        );
    }

    @DisplayName("멤버십 적립")
    @Test
    void spend_MembershipExists_Success() {
        // given
        MembershipUpdateRequest request = MembershipUpdateRequest.builder()
            .membershipId(membership.getUid())
            .amount(100_000)
            .build();
        assertEquals(5210, membership.getPoint());
        given(membershipRepository.findByUid(membership.getUid()))
            .willReturn(Optional.of(membership));

        // when
        boolean success = membershipService.spend(request);

        // then
        assertTrue(success);
        assertEquals(6210, membership.getPoint());
    }

    @DisplayName("멤버십 불러오기 - 해당 멤버십이 존재하지 않으면 예외가 발생한다.")
    @Test
    void getMembership_MembershipNoExists_ExceptionThrown() {
        given(membershipRepository.findByUid(any())).willReturn(Optional.empty());

        assertThrows(MembershipNotFoundException.class,
            () -> membershipService.findOne(membership.getUid()));
    }
}