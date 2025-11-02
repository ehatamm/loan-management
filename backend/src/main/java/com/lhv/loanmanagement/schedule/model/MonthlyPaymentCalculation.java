package com.lhv.loanmanagement.schedule.model;

import com.lhv.loanmanagement.schedule.FinancialCalculationConstants;
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
    
    public MonthlyPaymentCalculation adjustForNegativeBalance() {
        if (balanceAfter.compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal adjustedPrincipal = principal.add(balanceAfter, MATH_CONTEXT);
            return new MonthlyPaymentCalculation(
                balanceBefore,
                interest,
                adjustedPrincipal,
                BigDecimal.ZERO.setScale(RESULT_SCALE, ROUNDING_MODE)
            );
        }
        return this;
    }
}

