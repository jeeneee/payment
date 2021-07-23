package com.kakao.payment.membership.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakao.payment.IntegrationTest;
import com.kakao.payment.membership.domain.Name;
import com.kakao.payment.membership.domain.Status;
import com.kakao.payment.membership.dto.MembershipResponse;
import com.kakao.payment.membership.dto.MembershipSaveRequest;
import com.kakao.payment.membership.dto.MembershipUpdateRequest;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MembershipIntegrationTest extends IntegrationTest {

    @Autowired
    private ObjectMapper mapper;

    @DisplayName("멤버십 전체 조회")
    @Test
    void findAll() throws JsonProcessingException {
        createMembership("test1", "mem1");
        Response commonResponse = get("/api/v1/membership", "test1");
        MembershipResponse response = commonResponse.jsonPath()
            .getObject("response", MembershipResponse[].class)[0];

        assertAll(
            () -> assertEquals("mem1", response.getUid()),
            () -> assertEquals("happypoint", response.getName()),
            () -> assertEquals(120, response.getPoint()),
            () -> assertNotNull(response.getStartDate()),
            () -> assertEquals(Status.Y, response.getStatus()),
            () -> assertEquals("test1", response.getOwnerUid())
        );
    }

    @DisplayName("멤버십 등록")
    @Test
    void save() throws JsonProcessingException {
        MembershipSaveRequest request = MembershipSaveRequest.builder()
            .uid("mem2")
            .name("happypoint")
            .point(5210)
            .build();
        String body = mapper.writeValueAsString(request);

        Response commonResponse = post("/api/v1/membership", "test2", body);

        MembershipResponse response = commonResponse.jsonPath()
            .getObject("response", MembershipResponse.class);

        assertAll(
            () -> assertEquals("mem2", response.getUid()),
            () -> assertEquals("happypoint", response.getName()),
            () -> assertEquals(5210, response.getPoint()),
            () -> assertNotNull(response.getStartDate()),
            () -> assertEquals(Status.Y, response.getStatus()),
            () -> assertEquals("test2", response.getOwnerUid())
        );
    }

    @DisplayName("멤버십 삭제")
    @Test
    void delete() throws JsonProcessingException {
        createMembership("test3", "mem3");
        Response commonResponse = delete("/api/v1/membership/" + "mem3", "test3");
        Boolean response = commonResponse.jsonPath().getObject("response", Boolean.class);

        assertTrue(response);
    }

    @DisplayName("멤버십 상세 조회")
    @Test
    void findOne() throws JsonProcessingException {
        createMembership("test4", "mem4");
        Response commonResponse = get("/api/v1/membership/" + "mem4", "test4");
        MembershipResponse response = commonResponse.jsonPath()
            .getObject("response", MembershipResponse.class);

        assertAll(
            () -> assertEquals("mem4", response.getUid()),
            () -> assertEquals("happypoint", response.getName()),
            () -> assertEquals(120, response.getPoint()),
            () -> assertNotNull(response.getStartDate()),
            () -> assertEquals(Status.Y, response.getStatus()),
            () -> assertEquals("test4", response.getOwnerUid())
        );
    }

    @DisplayName("멤버십 포인트 적립")
    @Test
    void update() throws JsonProcessingException {
        createMembership("test5", "mem5");
        MembershipUpdateRequest request = MembershipUpdateRequest.builder()
            .uid("mem5")
            .amount(100)
            .build();
        String body = mapper.writeValueAsString(request);

        Response commonResponse = put("/api/v1/membership/point", "test5", body);
        Boolean response = commonResponse.jsonPath().getObject("response", Boolean.class);

        assertTrue(response);
    }

    private void createMembership(String userId, String membershipId)
        throws JsonProcessingException {
        MembershipSaveRequest request = MembershipSaveRequest.builder()
            .uid(membershipId)
            .name(Name.SPC.getValue())
            .point(120)
            .build();
        String body = mapper.writeValueAsString(request);
        post("/api/v1/membership", userId, body);
    }
}
