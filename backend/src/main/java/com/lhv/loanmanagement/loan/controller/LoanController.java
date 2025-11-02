package com.lhv.loanmanagement.loan.controller;

import com.lhv.loanmanagement.loan.Loan;
import com.lhv.loanmanagement.loan.dto.CreateLoanRequest;
import com.lhv.loanmanagement.loan.dto.LoanResponse;
import com.lhv.loanmanagement.loan.dto.ScheduleResponse;
import com.lhv.loanmanagement.loan.service.LoanService;
import com.lhv.loanmanagement.schedule.service.RepaymentScheduleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;
    private final RepaymentScheduleService repaymentScheduleService;

    public LoanController(LoanService loanService, RepaymentScheduleService repaymentScheduleService) {
        this.loanService = loanService;
        this.repaymentScheduleService = repaymentScheduleService;
    }

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody CreateLoanRequest request) {
        log.debug("Received loan creation request: type={}, amount={}", request.getLoanType(), request.getAmount());
        
        Loan loan = request.toEntity();
        Loan saved = loanService.create(loan);
        LoanResponse response = LoanResponse.from(saved);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponse> getLoan(@PathVariable UUID id) {
        log.debug("Received request to get loan with id={}", id);
        
        Loan loan = loanService.findById(id);
        LoanResponse response = LoanResponse.from(loan);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/schedule")
    public ResponseEntity<ScheduleResponse> getSchedule(@PathVariable UUID id) {
        log.debug("Received request to get schedule for loan id={}", id);
        
        Loan loan = loanService.findById(id);
        ScheduleResponse response = ScheduleResponse.builder()
                .items(repaymentScheduleService.calculateSchedule(loan))
                .build();
        
        return ResponseEntity.ok(response);
    }
}

