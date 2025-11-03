package com.lhv.loanmanagement.schedule.calculator;

import com.lhv.loanmanagement.loan.Loan;
import com.lhv.loanmanagement.schedule.model.MonthlyPaymentCalculation;
import com.lhv.loanmanagement.schedule.model.ScheduleAccumulator;
import com.lhv.loanmanagement.schedule.model.ScheduleItem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static com.lhv.loanmanagement.schedule.FinancialCalculationConstants.*;

public abstract class AbstractScheduleCalculator implements ScheduleCalculator {

    protected BigDecimal calculateMonthlyRate(BigDecimal annualRate) {
        return annualRate
                .divide(BigDecimal.valueOf(PERCENTAGE_DIVISOR), MATH_CONTEXT)
                .divide(BigDecimal.valueOf(MONTHS_PER_YEAR), MATH_CONTEXT);
    }

    protected BigDecimal calculateMonthlyInterest(BigDecimal balance, BigDecimal monthlyRate) {
        return balance.multiply(monthlyRate, MATH_CONTEXT);
    }

    protected List<ScheduleItem> buildScheduleItems(Loan loan, BigDecimal monthlyRate, Function<BigDecimal, MonthlyPaymentCalculation> calculator, BigDecimal constantPayment) {
        BigDecimal initialBalance = loan.getAmount().setScale(CALCULATION_SCALE, ROUNDING_MODE);
        BigDecimal loanAmount = loan.getAmount();
        int periodMonths = loan.getPeriodMonths();
        LocalDate startDate = loan.getStartDate();
        
        return Stream.iterate(startDate, date -> date.plusMonths(1))
                .limit(periodMonths)
                .collect(
                    Collector.of(
                        () -> new ScheduleAccumulator(initialBalance),
                        (acc, paymentDate) -> {
                            boolean isLastPayment = acc.getItems().size() == periodMonths - 1;
                            MonthlyPaymentCalculation calculation = calculator.apply(acc.getBalance());
                            
                            if (isLastPayment) {
                                calculation = adjustLastPayment(calculation, acc, loanAmount, monthlyRate);
                            }
                            
                            ScheduleItem item = toScheduleItem(paymentDate, calculation, isLastPayment, constantPayment);
                            acc.addItem(item, calculation.getBalanceAfter());
                        },
                        (acc1, acc2) -> {
                            throw new UnsupportedOperationException("Parallel streams not supported");
                        },
                        ScheduleAccumulator::getItems
                    )
                );
    }

    private MonthlyPaymentCalculation adjustLastPayment(
            MonthlyPaymentCalculation calculation,
            ScheduleAccumulator accumulator,
            BigDecimal loanAmount,
            BigDecimal monthlyRate) {
        
        BigDecimal remainingPrincipal = loanAmount.subtract(accumulator.getAccumulatedPrincipal());
        BigDecimal interest = calculateMonthlyInterest(accumulator.getBalance(), monthlyRate);
        
        BigDecimal roundedInterest = interest.setScale(RESULT_SCALE, ROUNDING_MODE);
        BigDecimal lastPaymentPrincipal = remainingPrincipal.setScale(RESULT_SCALE, ROUNDING_MODE);
        
        return new MonthlyPaymentCalculation(
            accumulator.getBalance(),
            roundedInterest,
            lastPaymentPrincipal,
            BigDecimal.ZERO.setScale(RESULT_SCALE, ROUNDING_MODE)
        );
    }

    protected ScheduleItem toScheduleItem(LocalDate paymentDate, MonthlyPaymentCalculation calculation, boolean isLastPayment, BigDecimal constantPayment) {
        BigDecimal roundedPrincipal = calculation.getPrincipal().setScale(RESULT_SCALE, ROUNDING_MODE);
        BigDecimal roundedInterest = calculation.getInterest().setScale(RESULT_SCALE, ROUNDING_MODE);
        BigDecimal payment = roundedPrincipal.add(roundedInterest);
        BigDecimal roundedBalance = calculation.getBalanceAfter().setScale(RESULT_SCALE, ROUNDING_MODE);
        
        return ScheduleItem.builder()
                .paymentDate(paymentDate)
                .payment(payment)
                .principal(roundedPrincipal)
                .interest(roundedInterest)
                .remainingBalance(roundedBalance)
                .build();
    }
}

