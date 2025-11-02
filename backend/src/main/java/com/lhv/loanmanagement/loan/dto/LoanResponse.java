package com.lhv.loanmanagement.loan.dto;

import com.lhv.loanmanagement.loan.enums.LoanType;
import com.lhv.loanmanagement.loan.enums.ScheduleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanResponse {

    private UUID id;
    private LoanType loanType;
    private BigDecimal amount;
    private Integer periodMonths;
    private BigDecimal annualInterestRate;
    private ScheduleType scheduleType;
    private LocalDate startDate;
}

