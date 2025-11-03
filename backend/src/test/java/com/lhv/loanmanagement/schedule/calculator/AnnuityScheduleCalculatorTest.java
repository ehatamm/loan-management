package com.lhv.loanmanagement.schedule.calculator;

import com.lhv.loanmanagement.loan.Loan;
import com.lhv.loanmanagement.loan.enums.LoanType;
import com.lhv.loanmanagement.loan.enums.ScheduleType;
import com.lhv.loanmanagement.schedule.model.ScheduleItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnnuityScheduleCalculatorTest {

    private AnnuityScheduleCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new AnnuityScheduleCalculator();
    }

    @Test
    @DisplayName("Should calculate annuity schedule with correct totals")
    void shouldCalculateAnnuityScheduleWithCorrectTotals() {
        // TODO: This test currently fails due to precision mismatch in last payment calculation.
        // Problem: The accumulator tracks balance at high precision (scale 10), while accumulatedPrincipal
        // sums rounded principals (scale 2). When adjusting the last payment, using balanceBefore from
        // high-precision accumulator vs remainingPrincipal from rounded sum creates inconsistency.
        // The last payment adjustment needs to ensure total principal equals loan amount exactly,
        // while maintaining correct interest calculation based on the actual remaining balance.
        
        // Given: 1000 EUR loan, 12 months, 5% annual rate
        Loan loan = Loan.builder()
                .id(java.util.UUID.randomUUID())
                .loanType(LoanType.CONSUMER)
                .amount(new BigDecimal("1000.00"))
                .periodMonths(12)
                .annualInterestRate(new BigDecimal("5.00"))
                .scheduleType(ScheduleType.ANNUITY)
                .startDate(LocalDate.of(2024, 1, 1))
                .build();

        // When
        List<ScheduleItem> schedule = calculator.calculate(loan);

        // Then
        assertThat(schedule).hasSize(12);
        
        BigDecimal totalPrincipal = schedule.stream()
                .map(ScheduleItem::getPrincipal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalInterest = schedule.stream()
                .map(ScheduleItem::getInterest)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalPayments = schedule.stream()
                .map(ScheduleItem::getPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total principal should equal loan amount
        assertThat(totalPrincipal).isEqualByComparingTo(new BigDecimal("1000.00"));
        
        // Total payments should equal principal + interest
        assertThat(totalPayments).isEqualByComparingTo(totalPrincipal.add(totalInterest));
        
        // Final balance should be zero
        ScheduleItem lastPayment = schedule.get(schedule.size() - 1);
        assertThat(lastPayment.getRemainingBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should have equal monthly payments for annuity schedule")
    void shouldHaveEqualMonthlyPayments() {
        // Given
        Loan loan = Loan.builder()
                .id(java.util.UUID.randomUUID())
                .loanType(LoanType.CAR)
                .amount(new BigDecimal("10000.00"))
                .periodMonths(24)
                .annualInterestRate(new BigDecimal("6.00"))
                .scheduleType(ScheduleType.ANNUITY)
                .startDate(LocalDate.of(2024, 1, 1))
                .build();

        // When
        List<ScheduleItem> schedule = calculator.calculate(loan);

        // Then: All payments should be equal (within rounding precision)
        BigDecimal firstPayment = schedule.get(0).getPayment();
        for (int i = 1; i < schedule.size(); i++) {
            assertThat(schedule.get(i).getPayment())
                    .isEqualByComparingTo(firstPayment);
        }
    }

    @Test
    @DisplayName("Should have decreasing interest and increasing principal over time")
    void shouldHaveDecreasingInterestAndIncreasingPrincipal() {
        // Given: Longer term loan ensures interest > principal in early payments
        Loan loan = Loan.builder()
                .id(java.util.UUID.randomUUID())
                .loanType(LoanType.MORTGAGE)
                .amount(new BigDecimal("100000.00"))
                .periodMonths(360) // 30 years
                .annualInterestRate(new BigDecimal("4.50"))
                .scheduleType(ScheduleType.ANNUITY)
                .startDate(LocalDate.of(2024, 1, 1))
                .build();

        // When
        List<ScheduleItem> schedule = calculator.calculate(loan);

        // Then: First payment should have more interest than principal
        ScheduleItem firstPayment = schedule.get(0);
        assertThat(firstPayment.getInterest()).isGreaterThan(firstPayment.getPrincipal());
        
        // Then: Last payment should have more principal than interest (or close to equal)
        ScheduleItem lastPayment = schedule.get(schedule.size() - 1);
        assertThat(lastPayment.getPrincipal()).isGreaterThanOrEqualTo(lastPayment.getInterest());
        
        // Then: Interest should decrease over time
        for (int i = 0; i < schedule.size() - 1; i++) {
            assertThat(schedule.get(i).getInterest())
                    .isGreaterThanOrEqualTo(schedule.get(i + 1).getInterest());
        }
        
        // Then: Principal should increase over time
        for (int i = 0; i < schedule.size() - 1; i++) {
            assertThat(schedule.get(i).getPrincipal())
                    .isLessThanOrEqualTo(schedule.get(i + 1).getPrincipal());
        }
    }

    @Test
    @DisplayName("Should calculate schedule with correct payment dates")
    void shouldCalculateScheduleWithCorrectPaymentDates() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 3, 15);
        Loan loan = Loan.builder()
                .id(java.util.UUID.randomUUID())
                .loanType(LoanType.CONSUMER)
                .amount(new BigDecimal("5000.00"))
                .periodMonths(6)
                .annualInterestRate(new BigDecimal("8.00"))
                .scheduleType(ScheduleType.ANNUITY)
                .startDate(startDate)
                .build();

        // When
        List<ScheduleItem> schedule = calculator.calculate(loan);

        // Then
        assertThat(schedule).hasSize(6);
        
        // Payment dates should increment monthly from start date
        for (int i = 0; i < schedule.size(); i++) {
            LocalDate expectedDate = startDate.plusMonths(i);
            assertThat(schedule.get(i).getPaymentDate()).isEqualTo(expectedDate);
        }
    }

    @Test
    @DisplayName("Should calculate exact totals for large loan amount and long term")
    void shouldCalculateExactTotalsForLargeLoanAndLongTerm() {
        // TODO: This test currently fails due to precision mismatch in last payment calculation.
        // Problem: The accumulator tracks balance at high precision (scale 10), while accumulatedPrincipal
        // sums rounded principals (scale 2). When adjusting the last payment, using balanceBefore from
        // high-precision accumulator vs remainingPrincipal from rounded sum creates inconsistency.
        // The last payment adjustment needs to ensure total principal equals loan amount exactly,
        // while maintaining correct interest calculation based on the actual remaining balance.
        
        // Given: 100,000 EUR loan, 30 years (360 months), 4.5% annual rate
        Loan loan = Loan.builder()
                .id(java.util.UUID.randomUUID())
                .loanType(LoanType.MORTGAGE)
                .amount(new BigDecimal("100000.00"))
                .periodMonths(360)
                .annualInterestRate(new BigDecimal("4.50"))
                .scheduleType(ScheduleType.ANNUITY)
                .startDate(LocalDate.of(2024, 1, 1))
                .build();

        // When
        List<ScheduleItem> schedule = calculator.calculate(loan);

        // Then
        assertThat(schedule).hasSize(360);
        
        BigDecimal totalPrincipal = schedule.stream()
                .map(ScheduleItem::getPrincipal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalInterest = schedule.stream()
                .map(ScheduleItem::getInterest)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalPayments = schedule.stream()
                .map(ScheduleItem::getPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total principal should equal loan amount exactly
        assertThat(totalPrincipal).isEqualByComparingTo(new BigDecimal("100000.00"));
        
        // Total payments should equal principal + interest exactly
        assertThat(totalPayments).isEqualByComparingTo(totalPrincipal.add(totalInterest));
        
        // Final balance should be exactly zero
        ScheduleItem lastPayment = schedule.get(schedule.size() - 1);
        assertThat(lastPayment.getRemainingBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
