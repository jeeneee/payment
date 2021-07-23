package com.kakao.payment.membership.service;

import com.kakao.payment.common.exception.PrincipalException;
import com.kakao.payment.membership.domain.Membership;
import com.kakao.payment.membership.domain.MembershipRepository;
import com.kakao.payment.membership.domain.Name;
import com.kakao.payment.membership.dto.MembershipResponse;
import com.kakao.payment.membership.dto.MembershipSaveRequest;
import com.kakao.payment.membership.dto.MembershipUpdateRequest;
import com.kakao.payment.membership.exception.DuplicatedMembershipException;
import com.kakao.payment.membership.exception.MembershipNotFoundException;
import com.kakao.payment.user.domain.User;
import com.kakao.payment.user.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserService userService;

    public MembershipResponse findOne(String uid, String ownerUid) {
        Membership membership = getMembership(uid);
        verifyOwner(membership, ownerUid);
        return MembershipResponse.from(membership);
    }

    public List<MembershipResponse> findAll(String ownerUid) {
        User owner = userService.getUser(ownerUid);
        List<Membership> memberships = owner.getMemberships();
        return memberships.stream()
            .map(MembershipResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public MembershipResponse save(MembershipSaveRequest request, String ownerUid) {
        validateMembershipId(request.getUid());
        User owner = userService.findUser(ownerUid).orElseGet(() -> userService.save(ownerUid));
        validateMembershipName(owner, Name.find(request.getName()));
        Membership membership = membershipRepository.save(request.toEntity(owner));
        return MembershipResponse.from(membership);
    }

    @Transactional
    public boolean deactivate(String membershipUid, String ownerUid) {
        Membership membership = getMembership(membershipUid);
        verifyOwner(membership, ownerUid);
        membership.deactivate();
        return true;
    }

    @Transactional
    public boolean spend(MembershipUpdateRequest request, String ownerUid) {
        Membership membership = getMembership(request.getUid());
        if (membership.isDeactivated()) {
            throw new MembershipNotFoundException("The membership already deactivated");
        }
        verifyOwner(membership, ownerUid);
        membership.spend(request.getAmount());
        return true;
    }

    private Membership getMembership(String uid) {
        return membershipRepository.findByUid(uid)
            .orElseThrow(() -> new MembershipNotFoundException("The membership does not exist."));
    }

    private void validateMembershipId(String uid) {
        if (membershipRepository.existsByUid(uid)) {
            throw new DuplicatedMembershipException("The membership id already exists.");
        }
    }

    private void validateMembershipName(User owner, Name name) {
        if (owner.hasSameMembership(name)) {
            throw new DuplicatedMembershipException("The membership name already exists.");
        }
    }

    private void verifyOwner(Membership membership, String ownerUid) {
        if (!isMembershipOwner(membership, ownerUid)) {
            throw new PrincipalException("The user is not owner of the membership.");
        }
    }

    private boolean isMembershipOwner(Membership membership, String ownerUid) {
        return membership.getOwnerUid().equals(ownerUid);
    }
}
