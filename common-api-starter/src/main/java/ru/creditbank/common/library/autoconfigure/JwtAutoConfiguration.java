package ru.creditbank.common.library.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import ru.creditbank.common.library.jwt.JwtStore;
import ru.creditbank.common.library.service.JwtSecurityContextService;
import ru.creditbank.common.library.service.JwtService;

@AutoConfiguration
public class JwtAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JwtStore jwtStore() {
        return new JwtStore();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtService jwtService() {
        return new JwtService();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtSecurityContextService jwtSecurityContextService() {
        return new JwtSecurityContextService();
    }
}
