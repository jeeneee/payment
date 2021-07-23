package com.kakao.payment.membership.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import com.kakao.payment.common.exception.PrincipalException;
import com.kakao.payment.membership.domain.Membership;
import com.kakao.payment.membership.domain.MembershipRepository;
import com.kakao.payment.membership.domain.Name;
import com.kakao.payment.membership.domain.Status;
import com.kakao.payment.membership.dto.MembershipResponse;
import com.kakao.payment.membership.dto.MembershipSaveRequest;
import com.kakao.payment.membership.dto.MembershipUpdateRequest;
import com.kakao.payment.membership.exception.DuplicatedMembershipException;
import com.kakao.payment.user.domain.User;
import com.kakao.payment.user.service.UserService;
import java.util.List;
import java.util.Optional;
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

    @Mock
    private UserService userService;

    private Membership membership;
    private Membership membership2;
    private User owner;

    @BeforeEach
    void setUp() {
        membershipService = new MembershipService(membershipRepository, userService);

        owner = User.builder()
            .id(1L)
            .uid("test1")
            .build();
        membership = Membership.builder()
            .id(1L)
            .uid("cj")
            .name(Name.CJ)
            .status(Status.Y)
            .point(5210)
            .owner(owner)
            .build();
        membership2 = Membership.builder()
            .id(2L)
            .uid("cj2")
            .name(membership.getName())
            .status(Status.Y)
            .owner(owner)
            .point(100)
            .build();
    }

    @DisplayName("멤버십 전체 조회")
    @Test
    void findAll_MembershipsExist_Success() {
        // given
        owner.getMemberships().add(membership);
        given(userService.getUser(any())).willReturn(owner);

        // when
        List<MembershipResponse> responses = membershipService.findAll(owner.getUid());

        // then
        MembershipResponse response = responses.get(0);
        assertAll(
            () -> assertEquals(1, response.getSeq()),
            () -> assertEquals(membership.getUid(), response.getUid()),
            () -> assertEquals(membership.getName().getValue(), response.getName()),
            () -> assertEquals(membership.getStatus(), response.getStatus()),
            () -> assertEquals(membership.getPoint(), response.getPoint()),
            () -> assertEquals(membership.getOwnerUid(), response.getOwnerUid())
        );
    }

    @DisplayName("멤버십 등록하기")
    @Test
    void save_MembershipNoExists_Success() {
        // given
        MembershipSaveRequest request = MembershipSaveRequest.builder()
            .uid(membership.getUid())
            .name(membership.getName().getValue())
            .point(membership.getPoint())
            .build();
        given(membershipRepository.existsByUid(any())).willReturn(false);
        given(userService.findUser(any())).willReturn(Optional.of(owner));
        given(membershipRepository.save(any(Membership.class))).willReturn(membership);

        // when
        MembershipResponse response = membershipService.save(request, owner.getUid());

        // then
        assertAll(
            () -> assertEquals(membership.getId(), response.getSeq()),
            () -> assertEquals(membership.getUid(), response.getUid()),
            () -> assertEquals(membership.getName().getValue(), response.getName()),
            () -> assertEquals(membership.getStatus(), response.getStatus()),
            () -> assertEquals(membership.getPoint(), response.getPoint()),
            () -> assertEquals(membership.getOwnerUid(), response.getOwnerUid())
        );
    }

    @DisplayName("멤버십 등록하기 - 해당 멤버십 아이디가 이미 존재하면 예외가 발생한다.")
    @Test
    void save_MembershipIdAlreadyExists_ExceptionThrown() {
        MembershipSaveRequest request = MembershipSaveRequest.builder()
            .uid(membership.getUid())
            .name(membership.getName().getValue())
            .point(membership.getPoint())
            .build();
        given(membershipRepository.existsByUid(any())).willReturn(true);

        assertThrows(DuplicatedMembershipException.class,
            () -> membershipService.save(request, owner.getUid()));
    }

    @DisplayName("멤버십 등록하기 - 같은 종류의 멤버십을 이미 소유하고 있으면 예외가 발생한다.")
    @Test
    void save_OwnerHadSameNameMembership_ExceptionThrown() {
        MembershipSaveRequest request = MembershipSaveRequest.builder()
            .uid(membership2.getUid())
            .name(membership2.getName().getValue())
            .point(membership2.getPoint())
            .build();
        owner.getMemberships().add(membership);
        given(membershipRepository.existsByUid(any())).willReturn(false);
        given(userService.findUser(any())).willReturn(Optional.of(owner));

        assertThrows(DuplicatedMembershipException.class,
            () -> membershipService.save(request, owner.getUid()));
    }

    @DisplayName("멤버십 삭제(비활성화)")
    @Test
    void deactivate_MembershipExists_Success() {
        // given
        assertEquals(Status.Y, membership.getStatus());
        given(membershipRepository.findByUid(any())).willReturn(Optional.of(membership));

        // when
        boolean success = membershipService.deactivate(membership.getUid(), owner.getUid());

        // then
        assertTrue(success);
        assertEquals(Status.N, membership.getStatus());
    }

    @DisplayName("멤버십 삭제(비활성화) - 멤버십 소유자가 아닌 경우 예외가 발생한다.")
    @Test
    void deactivate_IsNotOwner_ExceptionThrown() {
        assertEquals(Status.Y, membership.getStatus());
        given(membershipRepository.findByUid(any())).willReturn(Optional.of(membership));

        assertThrows(PrincipalException.class,
            () -> membershipService.deactivate(membership.getUid(), "test2"));
    }

    @DisplayName("멤버십 적립")
    @Test
    void spend_MembershipExists_Success() {
        // given
        MembershipUpdateRequest request = MembershipUpdateRequest.builder()
            .uid(membership.getUid())
            .amount(100_000)
            .build();
        assertEquals(5210, membership.getPoint());
        given(membershipRepository.findByUid(any())).willReturn(Optional.of(membership));

        // when
        boolean success = membershipService.spend(request, owner.getUid());

        // then
        assertTrue(success);
        assertEquals(6210, membership.getPoint());
    }

    @DisplayName("멤버십 적립 - 멤버십 소유자가 아닌 경우 예외가 발생한다.")
    @Test
    void spend_IsNotOwner_ExceptionThrown() {
        MembershipUpdateRequest request = MembershipUpdateRequest.builder()
            .uid(membership.getUid())
            .amount(100_000)
            .build();
        assertEquals(5210, membership.getPoint());
        given(membershipRepository.findByUid(any())).willReturn(Optional.of(membership));

        assertThrows(PrincipalException.class,
            () -> membershipService.spend(request, owner.getUid() + owner.getUid()));
    }

    @DisplayName("멤버십 상세 조회")
    @Test
    void findOne_MembershipExists_Success() {
        // given
        given(membershipRepository.findByUid(any())).willReturn(Optional.of(membership));

        // when
        MembershipResponse response = membershipService
            .findOne(membership.getUid(), owner.getUid());

        // then
        assertAll(
            () -> assertEquals(1, response.getSeq()),
            () -> assertEquals(membership.getUid(), response.getUid()),
            () -> assertEquals(membership.getName().getValue(), response.getName()),
            () -> assertEquals(membership.getStatus(), response.getStatus()),
            () -> assertEquals(membership.getPoint(), response.getPoint()),
            () -> assertEquals(membership.getOwnerUid(), response.getOwnerUid())
        );
    }
}