package ru.creditbank.credit.operations;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "test-local"})
public abstract class SpringBootMvcBaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @SneakyThrows
    protected <RQ> void performPathWithDto(String url, RQ rqDto, ResultMatcher expectedStatus, String token) {
        performPathWithDto(url, rqDto, null, expectedStatus, token);
    }

    @SneakyThrows
    protected <RQ, RS> RS performPathWithDto(String url, RQ rqDto, Class<RS> rsDtoClazz, ResultMatcher expectedStatus, String token) {
        var requestBuilder = patch(url);
        requestBuilder.contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rqDto));
        if (token != null) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }
        var result = mockMvc.perform(requestBuilder)
                .andExpect(expectedStatus)
                .andReturn();
        if (rsDtoClazz == null) return null;
        return objectMapper.readValue(result.getResponse().getContentAsString(), rsDtoClazz);
    }

    @SneakyThrows
    protected <RS> RS performGetWithDto(String url, Class<RS> rsDtoClazz, ResultMatcher expectedStatus, String token) {
        var requestBuilder = get(url);
        if (token != null) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }
        requestBuilder.contentType(MediaType.APPLICATION_JSON);
        var result = mockMvc.perform(requestBuilder)
                .andExpect(expectedStatus)
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), rsDtoClazz);
    }


    protected <RQ, RS> RS performPostWithDto(String url, RQ rqDto, Class<RS> rsDTOClazz, ResultMatcher expectedStatus) {
        return performPostWithDto(url, rqDto, rsDTOClazz, expectedStatus, null);
    }

    @SneakyThrows
    protected <RQ, RS> RS performPostWithDto(String url, RQ rqDto, Class<RS> rsDtoClazz, ResultMatcher expectedStatus, String token) {
        var result = mockMvc.perform(postWithDto(url, rqDto, token))
                .andExpect(expectedStatus)
                .andReturn();
        if (rsDtoClazz == null) return null;
        return objectMapper.readValue(result.getResponse().getContentAsString(), rsDtoClazz);
    }

    @SneakyThrows
    protected <RQ> MockHttpServletRequestBuilder postWithDto(String url, RQ rqDto, String token) {
        var requestBuilder = post(url);
        if (token != null) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }
        return requestBuilder.contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rqDto));
    }
}
