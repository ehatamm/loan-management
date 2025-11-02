package com.lhv.loanmanagement.loan.service;

import com.lhv.loanmanagement.loan.Loan;
import com.lhv.loanmanagement.loan.LoanRepository;
import com.lhv.loanmanagement.loan.exception.LoanNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class LoanService {

    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Transactional
    public Loan create(Loan loan) {
        Assert.notNull(loan, "Loan cannot be null");
        
        log.debug("Creating new loan: type={}, amount={}, periodMonths={}, interestRate={}, scheduleType={}, startDate={}",
                loan.getLoanType(), loan.getAmount(), loan.getPeriodMonths(),
                loan.getAnnualInterestRate(), loan.getScheduleType(), loan.getStartDate());

        Loan saved = loanRepository.save(loan);
        log.info("Created loan with id={}", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public Loan findById(UUID id) {
        Assert.notNull(id, "Loan ID cannot be null");
        
        log.debug("Finding loan by id={}", id);
        return loanRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Loan not found with id={}", id);
                    return new LoanNotFoundException("Loan not found with id: " + id);
                });
    }

    @Transactional(readOnly = true)
    public List<Loan> findAll() {
        log.debug("Finding all loans");
        List<Loan> loans = loanRepository.findAll();
        log.info("Found {} loans", loans.size());
        return loans;
    }
}

