package ru.creditbank.credit.operations.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import ru.creditbank.common.library.client.LoanManagementLoansServiceClient;
import ru.creditbank.common.library.client.LoanManagementPaymentsServiceClient;

@Configuration
@EnableFeignClients(clients = {
        LoanManagementPaymentsServiceClient.class,
        LoanManagementLoansServiceClient.class
})
@Slf4j
public class FeignConfig {

}