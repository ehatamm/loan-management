package com.lhv.loanmanagement.schedule.calculator;

import com.lhv.loanmanagement.loan.Loan;
import com.lhv.loanmanagement.loan.enums.ScheduleType;
import com.lhv.loanmanagement.schedule.model.MonthlyPaymentCalculation;
import com.lhv.loanmanagement.schedule.model.ScheduleItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import static com.lhv.loanmanagement.schedule.FinancialCalculationConstants.*;

@Component
public class EqualPrincipalScheduleCalculator extends AbstractScheduleCalculator {

    @Override
    public ScheduleType getScheduleType() {
        return ScheduleType.EQUAL_PRINCIPAL;
    }

    @Override
    public List<ScheduleItem> calculate(Loan loan) {
        BigDecimal monthlyRate = calculateMonthlyRate(loan.getAnnualInterestRate());
        BigDecimal fixedPrincipal = calculateFixedPrincipal(loan.getAmount(), loan.getPeriodMonths());
        
        Function<BigDecimal, MonthlyPaymentCalculation> calculator = 
            balance -> calculateMonthlyPayment(balance, monthlyRate, fixedPrincipal);
        
        return buildScheduleItems(loan, monthlyRate, calculator);
    }

    private BigDecimal calculateFixedPrincipal(BigDecimal principal, int periodMonths) {
        return principal
                .divide(BigDecimal.valueOf(periodMonths), MATH_CONTEXT)
                .setScale(RESULT_SCALE, ROUNDING_MODE);
    }

    private MonthlyPaymentCalculation calculateMonthlyPayment(BigDecimal balanceBefore,
                                                               BigDecimal monthlyRate,
                                                               BigDecimal fixedPrincipal) {
        BigDecimal interest = calculateMonthlyInterest(balanceBefore, monthlyRate);
        BigDecimal balanceAfter = balanceBefore.subtract(fixedPrincipal, MATH_CONTEXT);
        
        return new MonthlyPaymentCalculation(
            balanceBefore, interest, fixedPrincipal, balanceAfter);
    }
}

