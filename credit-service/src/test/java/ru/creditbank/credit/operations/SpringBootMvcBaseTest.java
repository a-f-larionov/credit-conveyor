package ru.creditbank.credit.operations;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
@ActiveProfiles("test")
public abstract class SpringBootMvcBaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected <RQ, RS> RS performPostWithDto(String url, RQ rqDto, Class<RS> rsDTOClazz, ResultMatcher expectedStatus) throws Exception {
        return performPostWithDto(url, rqDto, rsDTOClazz, expectedStatus, null);
    }

    protected <RQ, RS> RS performPostWithDto(String url, RQ rqDto, Class<RS> rsDtoClazz, ResultMatcher expectedStatus, String token) throws Exception {
        var result = mockMvc.perform(postWithDto(url, rqDto, token))
                .andExpect(expectedStatus)
                .andReturn();
        if (rsDtoClazz == null) return null;
        return objectMapper.readValue(result.getResponse().getContentAsString(), rsDtoClazz);
    }

    protected <RQ> MockHttpServletRequestBuilder postWithDto(String url, RQ rqDto, String token) throws JsonProcessingException {
        var requestBuilder = post(url);
        if (token != null) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }
        return requestBuilder.contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rqDto));
    }
}
