package com.lhv.loanmanagement.schedule.service;

import com.lhv.loanmanagement.loan.Loan;
import com.lhv.loanmanagement.schedule.FinancialCalculationConstants;
import com.lhv.loanmanagement.schedule.model.MonthlyPaymentCalculation;
import com.lhv.loanmanagement.schedule.model.ScheduleAccumulator;
import com.lhv.loanmanagement.schedule.model.ScheduleItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static com.lhv.loanmanagement.schedule.FinancialCalculationConstants.*;

@Service
public class RepaymentScheduleService {

    public List<ScheduleItem> calculateAnnuitySchedule(Loan loan) {
        BigDecimal monthlyRate = calculateMonthlyRate(loan.getAnnualInterestRate());
        BigDecimal payment = calculateAnnuityPayment(loan.getAmount(), monthlyRate, loan.getPeriodMonths());
        
        return buildScheduleItems(loan, monthlyRate, payment);
    }
    
    private BigDecimal calculateAnnuityPaymentUnrounded(BigDecimal principal, BigDecimal monthlyRate, int periodMonths) {
        // Annuity formula: P * r * (1+r)^n / ((1+r)^n - 1)
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate, MATH_CONTEXT);
        BigDecimal compoundFactor = onePlusRate.pow(periodMonths, MATH_CONTEXT);
        BigDecimal numerator = principal.multiply(monthlyRate, MATH_CONTEXT).multiply(compoundFactor, MATH_CONTEXT);
        BigDecimal denominator = compoundFactor.subtract(BigDecimal.ONE, MATH_CONTEXT);
        
        return numerator.divide(denominator, MATH_CONTEXT);
    }

    private BigDecimal calculateMonthlyRate(BigDecimal annualRate) {
        return annualRate
                .divide(BigDecimal.valueOf(PERCENTAGE_DIVISOR), MATH_CONTEXT)
                .divide(BigDecimal.valueOf(MONTHS_PER_YEAR), MATH_CONTEXT);
    }

    private BigDecimal calculateAnnuityPayment(BigDecimal principal, BigDecimal monthlyRate, int periodMonths) {
        return calculateAnnuityPaymentUnrounded(principal, monthlyRate, periodMonths);
    }

    private List<ScheduleItem> buildScheduleItems(Loan loan, BigDecimal monthlyRate, BigDecimal payment) {
        BigDecimal initialBalance = loan.getAmount().setScale(CALCULATION_SCALE, ROUNDING_MODE);
        int periodMonths = loan.getPeriodMonths();
        LocalDate startDate = loan.getStartDate();
        
        return Stream.iterate(startDate, date -> date.plusMonths(1))
                .limit(periodMonths)
                .collect(
                    Collector.of(
                        () -> new ScheduleAccumulator(initialBalance),
                        (acc, paymentDate) -> {
                            MonthlyPaymentCalculation calculation = calculateMonthlyPayment(
                                acc.getBalance(), monthlyRate, payment);
                            ScheduleItem item = buildScheduleItem(paymentDate, calculation);
                            acc.addItem(item, calculation.getBalanceAfter());
                        },
                        (acc1, acc2) -> {
                            throw new UnsupportedOperationException("Parallel streams not supported");
                        },
                        ScheduleAccumulator::getItems
                    )
                );
    }

    private MonthlyPaymentCalculation calculateMonthlyPayment(BigDecimal balanceBefore, 
                                                              BigDecimal monthlyRate, 
                                                              BigDecimal payment) {
        BigDecimal interest = calculateMonthlyInterest(balanceBefore, monthlyRate);
        BigDecimal principal = calculatePrincipalPayment(payment, interest);
        BigDecimal balanceAfter = balanceBefore.subtract(principal, MATH_CONTEXT);
        
        MonthlyPaymentCalculation calculation = new MonthlyPaymentCalculation(
            balanceBefore, interest, principal, balanceAfter);
        
        return calculation.adjustForNegativeBalance();
    }

    private BigDecimal calculateMonthlyInterest(BigDecimal balance, BigDecimal monthlyRate) {
        return balance.multiply(monthlyRate, MATH_CONTEXT);
    }

    private BigDecimal calculatePrincipalPayment(BigDecimal payment, BigDecimal interest) {
        return payment.subtract(interest, MATH_CONTEXT);
    }

    private ScheduleItem buildScheduleItem(LocalDate paymentDate, MonthlyPaymentCalculation calculation) {
        return ScheduleItem.builder()
                .paymentDate(paymentDate)
                .payment(calculation.getPayment().setScale(RESULT_SCALE, ROUNDING_MODE))
                .principal(calculation.getPrincipal().setScale(RESULT_SCALE, ROUNDING_MODE))
                .interest(calculation.getInterest().setScale(RESULT_SCALE, ROUNDING_MODE))
                .remainingBalance(calculation.getBalanceAfter().setScale(RESULT_SCALE, ROUNDING_MODE))
                .build();
    }
}

