package com.kakao.payment.membership;

import com.kakao.payment.membership.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/membership")
public class MembershipController {

    private final MembershipService membershipService;

//    @GetMapping
//    public ResponseEntity<CommonResponse> findAll() {
////        List<MembershipResponse> response = membershipService.findAllByOwner();
////        return ResponseEntity.ok(new CommonResponse(true, response, null));
//    }
//
//    @PostMapping
//    public ResponseEntity<CommonResponse> save(@RequestBody @Valid MembershipSaveRequest request,
//        @RequestHeader("X-USER-ID") String owner) {
////        MembershipResponse response = membershipService.save(request, owner);
////        return ResponseEntity.ok(new CommonResponse(true, response, null));
//    }
}
