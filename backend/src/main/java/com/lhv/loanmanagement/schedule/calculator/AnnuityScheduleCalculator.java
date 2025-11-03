package com.lhv.loanmanagement.schedule.calculator;

import com.lhv.loanmanagement.loan.Loan;
import com.lhv.loanmanagement.loan.enums.ScheduleType;
import com.lhv.loanmanagement.schedule.model.MonthlyPaymentCalculation;
import com.lhv.loanmanagement.schedule.model.ScheduleItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import static com.lhv.loanmanagement.schedule.FinancialCalculationConstants.*;

@Component
public class AnnuityScheduleCalculator extends AbstractScheduleCalculator {

    @Override
    public ScheduleType getScheduleType() {
        return ScheduleType.ANNUITY;
    }

    @Override
    public List<ScheduleItem> calculate(Loan loan) {
        BigDecimal monthlyRate = calculateMonthlyRate(loan.getAnnualInterestRate());
        BigDecimal payment = calculateAnnuityPayment(loan.getAmount(), monthlyRate, loan.getPeriodMonths());
        BigDecimal constantPayment = payment.setScale(RESULT_SCALE, ROUNDING_MODE);
        
        Function<BigDecimal, MonthlyPaymentCalculation> calculator = 
            balance -> calculateMonthlyPayment(balance, monthlyRate, payment);
        
        return buildScheduleItems(loan, monthlyRate, calculator, constantPayment);
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
        
        return new MonthlyPaymentCalculation(
            balanceBefore, interest, principal, balanceAfter);
    }

    @Override
    protected ScheduleItem toScheduleItem(LocalDate paymentDate, MonthlyPaymentCalculation calculation, boolean isLastPayment, BigDecimal constantPayment) {
        if (constantPayment != null && !isLastPayment) {
            BigDecimal roundedInterest = calculation.getInterest().setScale(RESULT_SCALE, ROUNDING_MODE);
            BigDecimal principal = constantPayment.subtract(roundedInterest, MATH_CONTEXT)
                    .setScale(RESULT_SCALE, ROUNDING_MODE);
            BigDecimal roundedBalance = calculation.getBalanceAfter().setScale(RESULT_SCALE, ROUNDING_MODE);
            
            return ScheduleItem.builder()
                    .paymentDate(paymentDate)
                    .payment(constantPayment)
                    .principal(principal)
                    .interest(roundedInterest)
                    .remainingBalance(roundedBalance)
                    .build();
        }
        
        return super.toScheduleItem(paymentDate, calculation, isLastPayment, constantPayment);
    }
}

