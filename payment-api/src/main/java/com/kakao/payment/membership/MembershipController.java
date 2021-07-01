package com.kakao.payment.membership;

import com.kakao.payment.common.dto.CommonResponse;
import com.kakao.payment.membership.dto.MembershipResponse;
import com.kakao.payment.membership.dto.MembershipSaveRequest;
import com.kakao.payment.membership.service.MembershipService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<CommonResponse> findAll() {
        List<MembershipResponse> response = membershipService.findAll();
        return ResponseEntity.ok(new CommonResponse(true, response, null));
    }

    @PostMapping
    public ResponseEntity<CommonResponse> save(@RequestBody @Valid MembershipSaveRequest request,
        @RequestHeader("X-USER-ID") String owner) {
        MembershipResponse response = membershipService.save(request, owner);
        return ResponseEntity.ok(new CommonResponse(true, response, null));
    }
}
