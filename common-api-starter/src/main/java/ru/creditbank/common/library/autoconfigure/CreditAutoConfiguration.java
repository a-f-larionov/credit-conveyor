package ru.creditbank.common.library.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import ru.creditbank.common.library.service.CreditCalculatorService;

@AutoConfiguration
public class CreditAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CreditCalculatorService creditCalculatorService() {
        return new CreditCalculatorService();
    }
}
