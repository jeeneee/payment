package com.kakao.payment;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    private static final String USER_ID_HEADER = "X-USER-ID";

    @Autowired
    protected ObjectMapper mapper;

    @LocalServerPort
    protected int port;

    private static RequestSpecification given() {
        return RestAssured.given()
            .contentType(APPLICATION_JSON_VALUE)
            .accept(APPLICATION_JSON_VALUE)
            .log().all();
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    protected Response get(String url, String userId) {
        return given().header(USER_ID_HEADER, userId)
            .when().get(url)
            .then().statusCode(HttpStatus.OK.value()).extract().response();
    }

    protected Response post(String url, String userId, String body) {
        return given().header(USER_ID_HEADER, userId).body(body)
            .when().post(url)
            .then().statusCode(HttpStatus.OK.value()).extract().response();
    }

    protected Response delete(String url, String userId) {
        return given().header(USER_ID_HEADER, userId)
            .when().delete(url)
            .then().statusCode(HttpStatus.OK.value()).extract().response();
    }

    protected Response put(String url, String userId, String body) {
        return given().header(USER_ID_HEADER, userId).body(body)
            .when().put(url)
            .then().statusCode(HttpStatus.OK.value()).extract().response();
    }
}
