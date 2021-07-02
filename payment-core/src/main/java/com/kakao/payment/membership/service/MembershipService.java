package com.kakao.payment.membership.service;

import com.kakao.payment.common.exception.PrincipalException;
import com.kakao.payment.membership.domain.Membership;
import com.kakao.payment.membership.domain.MembershipRepository;
import com.kakao.payment.membership.dto.MembershipSaveRequest;
import com.kakao.payment.membership.dto.MembershipUpdateRequest;
import com.kakao.payment.membership.exception.DuplicatedMembershipException;
import com.kakao.payment.membership.exception.MembershipNotFoundException;
import com.kakao.payment.user.domain.User;
import com.kakao.payment.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserService userService;

    public List<Membership> findAllByOwner(String ownerUid) {
        User owner = userService.findOne(ownerUid);
        return membershipRepository.findAllByOwner(owner);
    }

    @Transactional
    public Membership save(MembershipSaveRequest request, String ownerUid) {
        if (membershipRepository.existsByUid(request.getMembershipId())) {
            throw new DuplicatedMembershipException("The membership id already exists");
        }

        User owner = userService.findOrSave(ownerUid);
        return membershipRepository.save(request.toEntity(owner));
    }

    @Transactional
    public boolean deactivate(String membershipUid, String ownerUid) {
        Membership membership = findOne(membershipUid);
        verifyOwner(membership, ownerUid);
        membership.deactivate();
        return true;
    }

    @Transactional
    public boolean spend(MembershipUpdateRequest request, String ownerUid) {
        Membership membership = findOne(request.getMembershipId());
        verifyOwner(membership, ownerUid);
        membership.spend(request.getAmount());
        return true;
    }

    public Membership findOne(String uid) {
        return membershipRepository.findByUid(uid)
            .orElseThrow(() -> new MembershipNotFoundException("The membership does not exist."));
    }

    private void verifyOwner(Membership membership, String ownerUid) {
        if (!isMembershipOwner(membership, ownerUid)) {
            throw new PrincipalException("The user is not owner of the membership.");
        }
    }

    private boolean isMembershipOwner(Membership membership, String ownerUid) {
        return membership.getOwner().getUid().equals(ownerUid);
    }
}
