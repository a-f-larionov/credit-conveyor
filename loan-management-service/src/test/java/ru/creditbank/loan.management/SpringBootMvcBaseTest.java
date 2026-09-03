package ru.creditbank.loan.management;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "test-local"})
@Sql(scripts = "/sql/truncate-tables.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class SpringBootMvcBaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @SneakyThrows
    protected <RS> RS performGet(String url, Class<RS> rsDtoClazz, String token) {
        return performGet(url, rsDtoClazz, status().isOk(), token);
    }

    @SneakyThrows
    protected <RS> RS performGet(String url, Class<RS> rsDtoClazz, ResultMatcher expectedStatus, String token) {
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


    @SneakyThrows
    protected <RQ, RS> RS performPost(String url, RQ rqDto, Class<RS> rsDtoClazz, String token) {
        return performPost(url, rqDto, rsDtoClazz, status().isOk(), token);
    }

    @SneakyThrows
    protected <RQ, RS> RS performPost(String url, RQ rqDto, Class<RS> rsDtoClazz, ResultMatcher expectedStatus, String token) {
        var result = mockMvc.perform(post(url, rqDto, token))
                .andExpect(expectedStatus)
                .andReturn();
        if (rsDtoClazz == null) return null;
        return objectMapper.readValue(result.getResponse().getContentAsString(), rsDtoClazz);
    }

    @SneakyThrows
    protected <RQ> MockHttpServletRequestBuilder post(String url, RQ rqDto, String token) {
        var requestBuilder = MockMvcRequestBuilders.post(url);
        if (token != null) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }
        return requestBuilder.contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rqDto));
    }
}
