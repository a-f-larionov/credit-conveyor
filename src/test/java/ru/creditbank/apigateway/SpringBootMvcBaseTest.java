package ru.creditbank.apigateway;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public abstract class SpringBootMvcBaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;


    protected <RQ, RS> RS performPostWithDto(String url, RQ rqDto, Class<RS> rsDTOClazz, ResultMatcher expectedStatus) throws Exception {
        var result = mockMvc.perform(postWithDto(url, rqDto))
                .andExpect(expectedStatus)
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), rsDTOClazz);
    }

    protected <RQ> void performPostWithDto(String url, RQ rqDTO, ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(postWithDto(url, rqDTO))
                .andExpect(expectedStatus)
                .andReturn();
    }

    protected <RQ> MockHttpServletRequestBuilder postWithDto(String url, RQ rqDto) throws JsonProcessingException {
        return post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rqDto));
    }
}
