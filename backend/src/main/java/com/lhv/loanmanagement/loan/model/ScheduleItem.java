package com.lhv.loanmanagement.loan.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleItem {

    private Integer monthIndex;
    private BigDecimal payment;
    private BigDecimal principal;
    private BigDecimal interest;
    private BigDecimal remainingBalance;
}

