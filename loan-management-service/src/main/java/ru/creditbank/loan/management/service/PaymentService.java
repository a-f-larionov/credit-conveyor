package ru.creditbank.loan.management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.creditbank.loan.management.dto.rq.PaymentRqDto;
import ru.creditbank.loan.management.dto.rs.PaymentHistoryRsDto;
import ru.creditbank.loan.management.dto.rs.PaymentRsDto;
import ru.creditbank.loan.management.mappers.PaymentMapper;
import ru.creditbank.loan.management.repository.PaymentRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;

    public PaymentRsDto makePayment(PaymentRqDto rqDto) {
        return null;
    }

    public PaymentHistoryRsDto history(UUID userId) {
        return null;
    }
}
