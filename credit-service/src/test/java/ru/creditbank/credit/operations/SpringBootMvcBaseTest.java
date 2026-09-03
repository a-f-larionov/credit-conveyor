package ru.creditbank.credit.operations;


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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

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
    protected <RQ> void performPath(String url, RQ rqDto, ResultMatcher expectedStatus, String token) {
        performPath(url, rqDto, null, expectedStatus, token);
    }

    @SneakyThrows
    protected <RQ, RS> RS performPath(String url, RQ rqDto, Class<RS> rsDtoClazz, ResultMatcher expectedStatus, String token) {
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


    protected <RQ, RS> RS performPost(String url, RQ rqDto, Class<RS> rsDTOClazz, ResultMatcher expectedStatus) {
        return performPost(url, rqDto, rsDTOClazz, expectedStatus, null);
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
