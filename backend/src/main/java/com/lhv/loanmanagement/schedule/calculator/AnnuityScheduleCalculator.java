package com.lhv.loanmanagement.schedule.calculator;

import com.lhv.loanmanagement.loan.Loan;
import com.lhv.loanmanagement.schedule.model.MonthlyPaymentCalculation;
import com.lhv.loanmanagement.schedule.model.ScheduleItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import static com.lhv.loanmanagement.schedule.FinancialCalculationConstants.*;

@Component
public class AnnuityScheduleCalculator extends AbstractScheduleCalculator {

    @Override
    public List<ScheduleItem> calculate(Loan loan) {
        BigDecimal monthlyRate = calculateMonthlyRate(loan.getAnnualInterestRate());
        BigDecimal payment = calculateAnnuityPayment(loan.getAmount(), monthlyRate, loan.getPeriodMonths());
        
        Function<BigDecimal, MonthlyPaymentCalculation> calculator = 
            balance -> calculateMonthlyPayment(balance, monthlyRate, payment);
        
        return buildScheduleItems(loan, calculator);
    }

    private BigDecimal calculateAnnuityPayment(BigDecimal principal, BigDecimal monthlyRate, int periodMonths) {
        // Annuity formula: P * r * (1+r)^n / ((1+r)^n - 1)
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate, MATH_CONTEXT);
        BigDecimal compoundFactor = onePlusRate.pow(periodMonths, MATH_CONTEXT);
        BigDecimal numerator = principal.multiply(monthlyRate, MATH_CONTEXT).multiply(compoundFactor, MATH_CONTEXT);
        BigDecimal denominator = compoundFactor.subtract(BigDecimal.ONE, MATH_CONTEXT);
        
        return numerator.divide(denominator, MATH_CONTEXT);
    }

    private MonthlyPaymentCalculation calculateMonthlyPayment(BigDecimal balanceBefore, 
                                                               BigDecimal monthlyRate, 
                                                               BigDecimal payment) {
        BigDecimal interest = calculateMonthlyInterest(balanceBefore, monthlyRate);
        BigDecimal principal = payment.subtract(interest, MATH_CONTEXT);
        BigDecimal balanceAfter = balanceBefore.subtract(principal, MATH_CONTEXT);
        
        MonthlyPaymentCalculation calculation = new MonthlyPaymentCalculation(
            balanceBefore, interest, principal, balanceAfter);
        
        return calculation.adjustForNegativeBalance();
    }
}

