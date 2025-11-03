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
    
    public MonthlyPaymentCalculation adjustRemainingBalance() {
        BigDecimal roundedBalanceAfter = balanceAfter.setScale(RESULT_SCALE, ROUNDING_MODE);
        if (roundedBalanceAfter.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal adjustedPrincipal = principal.add(roundedBalanceAfter, MATH_CONTEXT);
            return new MonthlyPaymentCalculation(
                    balanceBefore,
                    interest,
                    adjustedPrincipal,
                    BigDecimal.ZERO.setScale(RESULT_SCALE, ROUNDING_MODE)
            );
        }
        return this;
    }
    
    public MonthlyPaymentCalculation withPrincipal(BigDecimal newPrincipal, BigDecimal monthlyRate) {
        BigDecimal interest = calculateMonthlyInterest(balanceBefore, monthlyRate);
        BigDecimal balanceAfter = balanceBefore.subtract(newPrincipal, MATH_CONTEXT);
        return new MonthlyPaymentCalculation(
                balanceBefore,
                interest,
                newPrincipal,
                balanceAfter
        ).adjustRemainingBalance();
    }
    
    private static BigDecimal calculateMonthlyInterest(BigDecimal balance, BigDecimal monthlyRate) {
        return balance.multiply(monthlyRate, MATH_CONTEXT);
    }
}

