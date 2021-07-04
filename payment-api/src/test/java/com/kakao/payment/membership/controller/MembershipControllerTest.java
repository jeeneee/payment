package com.kakao.payment.membership.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.kakao.payment.MockMvcTest;
import com.kakao.payment.common.dto.ErrorResponse;
import com.kakao.payment.membership.MembershipController;
import com.kakao.payment.membership.domain.Membership;
import com.kakao.payment.membership.domain.Name;
import com.kakao.payment.membership.domain.Status;
import com.kakao.payment.membership.dto.MembershipResponse;
import com.kakao.payment.membership.dto.MembershipSaveRequest;
import com.kakao.payment.membership.dto.MembershipUpdateRequest;
import com.kakao.payment.membership.service.MembershipService;
import com.kakao.payment.user.domain.User;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(MembershipController.class)
class MembershipControllerTest extends MockMvcTest {

    private static final String USER_ID = "test1";

    @MockBean
    private MembershipService membershipService;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = User.builder()
            .id(1L)
            .uid(USER_ID)
            .build();
    }

    @DisplayName("멤버십 전체 조회")
    @Test
    void findAll() throws Exception {
        Membership membership = Membership.builder()
            .id(1L)
            .uid("spc")
            .name(Name.SPC)
            .status(Status.Y)
            .point(120)
            .owner(owner)
            .build();
        List<MembershipResponse> membershipResponseList = Lists
            .list(MembershipResponse.from(1, membership));

        given(membershipService.findAll(any())).willReturn(membershipResponseList);

        MvcResult mvcResult = get("/api/v1/membership", USER_ID)
            .andExpect(status().isOk())
            .andReturn();

        JsonNode root = mapper.readTree(mvcResult.getResponse().getContentAsString());

        Boolean success = getField(root, "success", Boolean.class);
        MembershipResponse response = getField(root, "response", MembershipResponse[].class)[0];
        ErrorResponse error = getField(root, "error", ErrorResponse.class);

        assertAll(
            () -> assertTrue(success),
            () -> assertEquals(1, response.getSeq()),
            () -> assertEquals(membership.getUid(), response.getUid()),
            () -> assertEquals(membership.getName().getValue(), response.getName()),
            () -> assertEquals(membership.getStatus(), response.getStatus()),
            () -> assertEquals(membership.getPoint(), response.getPoint()),
            () -> assertEquals(membership.getOwnerUid(), response.getOwnerUid()),
            () -> assertNull(error)
        );
    }

    @DisplayName("멤버십 등록")
    @Test
    void save() throws Exception {
        MembershipSaveRequest request = MembershipSaveRequest.builder()
            .uid("cj")
            .name(Name.CJ.getValue())
            .point(5210)
            .build();
        String content = mapper.writeValueAsString(request);
        Membership membership = request.toEntity(owner);
        MembershipResponse membershipResponse = MembershipResponse.from(1, membership);

        given(membershipService.save(any(MembershipSaveRequest.class), any()))
            .willReturn(membershipResponse);

        MvcResult mvcResult = post("/api/v1/membership", USER_ID, content)
            .andExpect(status().isOk())
            .andReturn();

        JsonNode root = mapper.readTree(mvcResult.getResponse().getContentAsString());

        Boolean success = getField(root, "success", Boolean.class);
        MembershipResponse response = getField(root, "response", MembershipResponse.class);
        ErrorResponse error = getField(root, "error", ErrorResponse.class);

        assertAll(
            () -> assertTrue(success),
            () -> assertEquals(1, response.getSeq()),
            () -> assertEquals(membership.getUid(), response.getUid()),
            () -> assertEquals(membership.getName().getValue(), response.getName()),
            () -> assertEquals(membership.getStatus(), response.getStatus()),
            () -> assertEquals(membership.getPoint(), response.getPoint()),
            () -> assertEquals(membership.getOwnerUid(), response.getOwnerUid()),
            () -> assertNull(error)
        );
    }

    @DisplayName("멤버십 삭제")
    @Test
    void delete() throws Exception {
        given(membershipService.deactivate(any(), any())).willReturn(Boolean.TRUE);

        MvcResult mvcResult = delete("/api/v1/membership/spc", USER_ID)
            .andExpect(status().isOk())
            .andReturn();

        JsonNode root = mapper.readTree(mvcResult.getResponse().getContentAsString());

        Boolean success = getField(root, "success", Boolean.class);
        Boolean response = getField(root, "response", Boolean.class);
        ErrorResponse error = getField(root, "error", ErrorResponse.class);

        assertAll(
            () -> assertTrue(success),
            () -> assertTrue(response),
            () -> assertNull(error)
        );
    }

    @DisplayName("멤버십 상세 조회")
    @Test
    void findOne() throws Exception {
        Membership membership = Membership.builder()
            .id(1L)
            .uid("spc")
            .name(Name.SPC)
            .status(Status.Y)
            .point(120)
            .owner(owner)
            .build();
        MembershipResponse membershipResponse = MembershipResponse.from(1, membership);

        given(membershipService.findOne(any(), any())).willReturn(membershipResponse);

        MvcResult mvcResult = get("/api/v1/membership/spc", USER_ID)
            .andExpect(status().isOk())
            .andReturn();

        JsonNode root = mapper.readTree(mvcResult.getResponse().getContentAsString());

        Boolean success = getField(root, "success", Boolean.class);
        MembershipResponse response = getField(root, "response", MembershipResponse.class);
        ErrorResponse error = getField(root, "error", ErrorResponse.class);

        assertAll(
            () -> assertTrue(success),
            () -> assertEquals(1, response.getSeq()),
            () -> assertEquals(membership.getUid(), response.getUid()),
            () -> assertEquals(membership.getName().getValue(), response.getName()),
            () -> assertEquals(membership.getStatus(), response.getStatus()),
            () -> assertEquals(membership.getPoint(), response.getPoint()),
            () -> assertEquals(membership.getOwnerUid(), response.getOwnerUid()),
            () -> assertNull(error)
        );
    }

    @DisplayName("멤버십 포인트 적립")
    @Test
    void update() throws Exception {
        MembershipUpdateRequest request = MembershipUpdateRequest.builder()
            .uid("cj")
            .amount(100)
            .build();
        String content = mapper.writeValueAsString(request);
        given(membershipService.spend(any(MembershipUpdateRequest.class), any()))
            .willReturn(Boolean.TRUE);

        MvcResult mvcResult = put("/api/v1/membership/point", USER_ID, content)
            .andExpect(status().isOk())
            .andReturn();

        JsonNode root = mapper.readTree(mvcResult.getResponse().getContentAsString());

        Boolean success = getField(root, "success", Boolean.class);
        Boolean response = getField(root, "response", Boolean.class);
        ErrorResponse error = getField(root, "error", ErrorResponse.class);

        assertAll(
            () -> assertTrue(success),
            () -> assertTrue(response),
            () -> assertNull(error)
        );
    }
}