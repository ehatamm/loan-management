package com.lhv.loanmanagement.schedule.calculator;

import com.lhv.loanmanagement.loan.Loan;
import com.lhv.loanmanagement.schedule.FinancialCalculationConstants;
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

    protected List<ScheduleItem> buildScheduleItems(Loan loan, BigDecimal monthlyRate, Function<BigDecimal, MonthlyPaymentCalculation> calculator) {
        BigDecimal initialBalance = loan.getAmount().setScale(CALCULATION_SCALE, ROUNDING_MODE);
        BigDecimal loanAmount = loan.getAmount();
        int periodMonths = loan.getPeriodMonths();
        LocalDate startDate = loan.getStartDate();
        
        List<ScheduleItem> items = Stream.iterate(startDate, date -> date.plusMonths(1))
                .limit(periodMonths)
                .collect(
                    Collector.of(
                        () -> new ScheduleAccumulator(initialBalance),
                        (acc, paymentDate) -> {
                            boolean isLastPayment = acc.getItems().size() == periodMonths - 1;
                            MonthlyPaymentCalculation calculation;
                            
                            calculation = calculator.apply(acc.getBalance());
                            
                            if (isLastPayment) {
                                BigDecimal remainingPrincipal = loanAmount.subtract(acc.getAccumulatedPrincipal());
                                calculation = calculation.withPrincipal(remainingPrincipal, monthlyRate);
                            }
                            
                            ScheduleItem item = buildScheduleItem(paymentDate, calculation);
                            acc.addItem(item, calculation.getBalanceAfter());
                        },
                        (acc1, acc2) -> {
                            throw new UnsupportedOperationException("Parallel streams not supported");
                        },
                        ScheduleAccumulator::getItems
                    )
                );
        
        return items;
    }

    protected ScheduleItem buildScheduleItem(LocalDate paymentDate, MonthlyPaymentCalculation calculation) {
        return ScheduleItem.builder()
                .paymentDate(paymentDate)
                .payment(calculation.getPayment().setScale(RESULT_SCALE, ROUNDING_MODE))
                .principal(calculation.getPrincipal().setScale(RESULT_SCALE, ROUNDING_MODE))
                .interest(calculation.getInterest().setScale(RESULT_SCALE, ROUNDING_MODE))
                .remainingBalance(calculation.getBalanceAfter().setScale(RESULT_SCALE, ROUNDING_MODE))
                .build();
    }
}

