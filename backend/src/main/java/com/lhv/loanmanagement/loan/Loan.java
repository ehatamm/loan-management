package com.lhv.loanmanagement.loan;

import com.lhv.loanmanagement.loan.enums.LoanType;
import com.lhv.loanmanagement.loan.enums.ScheduleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "loan_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private LoanType loanType;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "period_months", nullable = false)
    private Integer periodMonths;

    @Column(name = "annual_interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal annualInterestRate;

    @Column(name = "schedule_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
}

