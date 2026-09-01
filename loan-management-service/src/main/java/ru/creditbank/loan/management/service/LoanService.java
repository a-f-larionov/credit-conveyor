package ru.creditbank.loan.management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.creditbank.loan.management.dto.rq.CreateLoanRqDto;
import ru.creditbank.loan.management.dto.rs.CreateLoanRsDto;
import ru.creditbank.loan.management.dto.rs.LoanListRsDto;
import ru.creditbank.loan.management.mappers.LoanMapper;
import ru.creditbank.loan.management.repository.LoanRepository;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanMapper loanMapper;
    private final LoanRepository loanRepository;

    public CreateLoanRsDto createLoan(CreateLoanRqDto rqDto) {
        return null;
    }

    public LoanListRsDto list(String userId) {
        return null;
    }
}
