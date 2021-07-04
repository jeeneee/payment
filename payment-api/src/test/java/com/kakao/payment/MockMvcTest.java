package com.kakao.payment;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

public abstract class MockMvcTest {

    private static final String USER_ID_HEADER = "X-USER-ID";

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext ctx;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
            .addFilters(new CharacterEncodingFilter("UTF-8", true))
            .alwaysDo(print())
            .build();
    }

    protected ResultActions get(String url, String userId) throws Exception {
        return this.mockMvc.perform(MockMvcRequestBuilders.get(url)
            .header(USER_ID_HEADER, userId)
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON));
    }

    protected ResultActions post(String url, String userId, String content) throws Exception {
        return this.mockMvc.perform(MockMvcRequestBuilders.post(url)
            .header(USER_ID_HEADER, userId)
            .content(content)
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON));
    }

    protected ResultActions delete(String url, String userId) throws Exception {
        return this.mockMvc.perform(MockMvcRequestBuilders.delete(url)
            .header(USER_ID_HEADER, userId)
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON));
    }

    protected ResultActions put(String url, String userId, String content)
        throws Exception {
        return this.mockMvc.perform(MockMvcRequestBuilders.put(url)
            .header(USER_ID_HEADER, userId)
            .content(content)
            .accept(APPLICATION_JSON)
            .contentType(APPLICATION_JSON));
    }

    protected <T> T getField(JsonNode root, String fieldName, Class<T> classType)
        throws JsonProcessingException {
        return mapper.treeToValue(root.get(fieldName), classType);
    }
}
