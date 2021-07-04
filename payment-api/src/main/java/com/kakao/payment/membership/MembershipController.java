package com.kakao.payment.membership;

import com.kakao.payment.common.dto.CommonResponse;
import com.kakao.payment.membership.dto.MembershipResponse;
import com.kakao.payment.membership.dto.MembershipSaveRequest;
import com.kakao.payment.membership.dto.MembershipUpdateRequest;
import com.kakao.payment.membership.service.MembershipService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/membership")
public class MembershipController {

    private final MembershipService membershipService;

    @GetMapping
    public ResponseEntity<CommonResponse> findAll(@RequestHeader("X-USER-ID") String ownerUid) {
        List<MembershipResponse> response = membershipService.findAll(ownerUid);
        return ResponseEntity.ok(new CommonResponse(true, response, null));
    }

    @PostMapping
    public ResponseEntity<CommonResponse> save(@RequestBody @Valid MembershipSaveRequest request,
        @RequestHeader("X-USER-ID") String ownerUid) {
        MembershipResponse response = membershipService.save(request, ownerUid);
        return ResponseEntity.ok(new CommonResponse(true, response, null));
    }

    @DeleteMapping("/{membershipId}")
    public ResponseEntity<CommonResponse> delete(@PathVariable String membershipId,
        @RequestHeader("X-USER-ID") String ownerUid) {
        boolean response = membershipService.deactivate(membershipId, ownerUid);
        return ResponseEntity.ok(new CommonResponse(true, response, null));
    }

    @GetMapping("/{membershipId}")
    public ResponseEntity<CommonResponse> findOne(@PathVariable String membershipId,
        @RequestHeader("X-USER-ID") String ownerUid) {
        MembershipResponse response = membershipService.findOne(membershipId, ownerUid);
        return ResponseEntity.ok(new CommonResponse(true, response, null));
    }

    @PutMapping("/point")
    public ResponseEntity<CommonResponse> update(
        @RequestBody @Valid MembershipUpdateRequest request,
        @RequestHeader("X-USER-ID") String ownerUid) {
        boolean response = membershipService.spend(request, ownerUid);
        return ResponseEntity.ok(new CommonResponse(true, response, null));
    }
}
