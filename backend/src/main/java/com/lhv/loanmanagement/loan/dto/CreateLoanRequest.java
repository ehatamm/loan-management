package com.lhv.loanmanagement.loan.dto;

import com.lhv.loanmanagement.loan.Loan;
import com.lhv.loanmanagement.loan.enums.LoanType;
import com.lhv.loanmanagement.loan.enums.ScheduleType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLoanRequest {

    @NotNull(message = "Loan type is required")
    private LoanType loanType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Amount must have at most 13 integer digits and 2 decimal places")
    private BigDecimal amount;

    @NotNull(message = "Period in months is required")
    @Min(value = 1, message = "Period must be at least 1 month")
    private Integer periodMonths;

    @NotNull(message = "Annual interest rate is required")
    @DecimalMin(value = "0.00", message = "Interest rate must be at least 0")
    @DecimalMax(value = "100.00", message = "Interest rate must be at most 100")
    @Digits(integer = 3, fraction = 2, message = "Interest rate must have at most 3 integer digits and 2 decimal places")
    private BigDecimal annualInterestRate;

    @NotNull(message = "Schedule type is required")
    private ScheduleType scheduleType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    public Loan toEntity() {
        return Loan.builder()
                .loanType(this.loanType)
                .amount(this.amount)
                .periodMonths(this.periodMonths)
                .annualInterestRate(this.annualInterestRate)
                .scheduleType(this.scheduleType)
                .startDate(this.startDate)
                .build();
    }
}

