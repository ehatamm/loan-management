package com.lhv.loanmanagement.schedule.model;

import lombok.Value;

import java.math.BigDecimal;

import static com.lhv.loanmanagement.schedule.FinancialCalculationConstants.*;

@Value
public class MonthlyPaymentCalculation {
    BigDecimal balanceBefore;
    BigDecimal interest;
    BigDecimal principal;
    BigDecimal balanceAfter;
    
    public BigDecimal getPayment() {
        return principal.add(interest, MATH_CONTEXT);
    }
}

