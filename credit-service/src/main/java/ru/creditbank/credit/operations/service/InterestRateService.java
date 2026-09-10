package ru.creditbank.credit.operations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.creditbank.credit.operations.enitity.CreditEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterestRateService {

    @Value("${credit.base-interest-rate}")
    private BigDecimal baseInterestRate;

    public BigDecimal calcInterestRate(CreditEntity creditEntity) {
        log.info("Calculate interest rate: creditId={} score={}", creditEntity.getId(), creditEntity.getScore());
        return baseInterestRate
                .add(riskBasedPricing(creditEntity));
    }

    private BigDecimal riskBasedPricing(CreditEntity creditEntity) {
        return BigDecimal.valueOf(creditEntity.getScore())
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    }
}
