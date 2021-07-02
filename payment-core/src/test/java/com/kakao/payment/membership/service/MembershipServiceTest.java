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
import com.kakao.payment.membership.dto.MembershipSaveRequest;
import com.kakao.payment.membership.dto.MembershipUpdateRequest;
import com.kakao.payment.membership.exception.DuplicatedMembershipException;
import com.kakao.payment.user.domain.User;
import com.kakao.payment.user.service.UserService;
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

    @Mock
    private UserService userService;

    private Membership membership;
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
    }

    @DisplayName("멤버십 전체 조회")
    @Test
    void findAllByOwner_MembershipsExist_Success() {
        // given
        List<Membership> memberships = Lists.list(membership);
        given(userService.findOne(owner.getUid())).willReturn(owner);
        given(membershipRepository.findAllByOwner(owner)).willReturn(memberships);

        // when
        List<Membership> foundList = membershipService.findAllByOwner(owner.getUid());

        // then
        Membership found = foundList.get(0);
        assertAll(
            () -> assertEquals(membership.getId(), found.getId()),
            () -> assertEquals(membership.getUid(), found.getUid()),
            () -> assertEquals(membership.getName(), found.getName()),
            () -> assertEquals(membership.getStatus(), found.getStatus()),
            () -> assertEquals(membership.getPoint(), found.getPoint()),
            () -> assertEquals(membership.getOwner(), found.getOwner())
        );
    }

    @DisplayName("멤버십 등록하기")
    @Test
    void save_MembershipNoExists_Success() {
        // given
        MembershipSaveRequest request = MembershipSaveRequest.builder()
            .membershipId(membership.getUid())
            .membershipName(membership.getName().getValue())
            .point(membership.getPoint())
            .build();
        given(membershipRepository.existsByUid(membership.getUid())).willReturn(false);
        given(userService.findOrSave(any())).willReturn(owner);
        given(membershipRepository.save(any(Membership.class))).willReturn(membership);

        // when
        Membership saved = membershipService.save(request, owner.getUid());

        // then
        assertAll(
            () -> assertEquals(membership.getId(), saved.getId()),
            () -> assertEquals(membership.getUid(), saved.getUid()),
            () -> assertEquals(membership.getName(), saved.getName()),
            () -> assertEquals(membership.getStatus(), saved.getStatus()),
            () -> assertEquals(membership.getPoint(), saved.getPoint()),
            () -> assertEquals(membership.getOwner(), saved.getOwner())
        );
    }

    @DisplayName("멤버십 등록하기 - 해당 멤버십이 이미 존재하면 예외가 발생한다.")
    @Test
    void save_MembershipAlreadyExists_ExceptionThrown() {
        MembershipSaveRequest request = MembershipSaveRequest.builder()
            .membershipId(membership.getUid())
            .membershipName(membership.getName().getValue())
            .point(membership.getPoint())
            .build();
        given(membershipRepository.existsByUid(membership.getUid())).willReturn(true);

        assertThrows(DuplicatedMembershipException.class,
            () -> membershipService.save(request, owner.getUid()));
    }

    @DisplayName("멤버십 삭제(비활성화)")
    @Test
    void deactivate_MembershipExists_Success() {
        // given
        assertEquals(Status.Y, membership.getStatus());
        given(membershipRepository.findByUid(membership.getUid()))
            .willReturn(Optional.of(membership));

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
        given(membershipRepository.findByUid(membership.getUid()))
            .willReturn(Optional.of(membership));

        assertThrows(PrincipalException.class,
            () -> membershipService
                .deactivate(membership.getUid(), owner.getUid() + owner.getUid()));
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
        boolean success = membershipService.spend(request, owner.getUid());

        // then
        assertTrue(success);
        assertEquals(6210, membership.getPoint());
    }

    @DisplayName("멤버십 적립 - 멤버십 소유자가 아닌 경우 예외가 발생한다.")
    @Test
    void spend_IsNotOwner_ExceptionThrown() {
        MembershipUpdateRequest request = MembershipUpdateRequest.builder()
            .membershipId(membership.getUid())
            .amount(100_000)
            .build();
        assertEquals(5210, membership.getPoint());
        given(membershipRepository.findByUid(membership.getUid()))
            .willReturn(Optional.of(membership));

        assertThrows(PrincipalException.class,
            () -> membershipService.spend(request, owner.getUid() + owner.getUid()));
    }

    @DisplayName("멤버십 상세 조회")
    @Test
    void findOne_MembershipExists_Success() {
        // given
        given(membershipRepository.findByUid(membership.getUid()))
            .willReturn(Optional.of(membership));

        // when
        Membership found = membershipService.findOne(membership.getUid());

        // then
        assertAll(
            () -> assertEquals(membership.getId(), found.getId()),
            () -> assertEquals(membership.getUid(), found.getUid()),
            () -> assertEquals(membership.getName(), found.getName()),
            () -> assertEquals(membership.getStatus(), found.getStatus()),
            () -> assertEquals(membership.getPoint(), found.getPoint()),
            () -> assertEquals(membership.getOwner(), found.getOwner())
        );
    }
}