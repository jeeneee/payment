package com.kakao.payment.membership.service;

import com.kakao.payment.membership.domain.Membership;
import com.kakao.payment.membership.domain.MembershipRepository;
import com.kakao.payment.membership.dto.MembershipResponse;
import com.kakao.payment.membership.dto.MembershipSaveRequest;
import com.kakao.payment.membership.dto.MembershipUpdateRequest;
import com.kakao.payment.membership.exception.DuplicatedMembershipException;
import com.kakao.payment.membership.exception.MembershipNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;

    public List<MembershipResponse> findAll() {
        List<Membership> memberships = membershipRepository.findAll();
        return IntStream.range(0, memberships.size())
            .mapToObj(i -> MembershipResponse.from(i + 1, memberships.get(i)))
            .collect(Collectors.toList());
    }

    public MembershipResponse save(MembershipSaveRequest request, String owner) {
        if (membershipRepository.findByUid(request.getMembershipId()).isPresent()) {
            throw new DuplicatedMembershipException("The membership id already exists");
        }
        Membership membership = membershipRepository.save(request.toEntity(owner));
        return MembershipResponse.from(1, membership);
    }

    public boolean deactivate(String uid) {
        Membership membership = getMembership(uid);
        membership.deactivate();
        return true;
    }

    public MembershipResponse findOne(String uid) {
        Membership membership = getMembership(uid);
        return MembershipResponse.from(1, membership);
    }

    public boolean spend(MembershipUpdateRequest request) {
        Membership membership = getMembership(request.getMembershipId());
        membership.spend(request.getAmount());
        return true;
    }

    public Membership getMembership(String uid) {
        return membershipRepository.findByUid(uid)
            .orElseThrow(() -> new MembershipNotFoundException("The membership does not exist."));
    }
}
