package com.kakao.payment.membership.domain;

import com.kakao.payment.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    List<Membership> findAllByOwner(User owner);

    Optional<Membership> findByUid(String uid);

    boolean existsByUid(String uid);
}
