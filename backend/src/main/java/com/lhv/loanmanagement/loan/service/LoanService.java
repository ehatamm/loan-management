package com.lhv.loanmanagement.loan.service;

import com.lhv.loanmanagement.loan.Loan;
import com.lhv.loanmanagement.loan.LoanRepository;
import com.lhv.loanmanagement.loan.dto.CreateLoanRequest;
import com.lhv.loanmanagement.loan.exception.LoanNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class LoanService {

    private static final Logger log = LoggerFactory.getLogger(LoanService.class);
    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Transactional
    public Loan create(CreateLoanRequest request) {
        log.debug("Creating new loan: type={}, amount={}, periodMonths={}, interestRate={}, scheduleType={}, startDate={}",
                request.getLoanType(), request.getAmount(), request.getPeriodMonths(),
                request.getAnnualInterestRate(), request.getScheduleType(), request.getStartDate());

        Loan loan = Loan.builder()
                .loanType(request.getLoanType())
                .amount(request.getAmount())
                .periodMonths(request.getPeriodMonths())
                .annualInterestRate(request.getAnnualInterestRate())
                .scheduleType(request.getScheduleType())
                .startDate(request.getStartDate())
                .build();

        Loan saved = loanRepository.save(loan);
        log.info("Created loan with id={}", saved.getId());
        return saved;
    }

    public Loan findById(UUID id) {
        log.debug("Finding loan by id={}", id);
        return loanRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Loan not found with id={}", id);
                    return new LoanNotFoundException("Loan not found with id: " + id);
                });
    }
}

