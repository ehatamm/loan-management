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

class EqualPrincipalScheduleCalculatorTest {

    private EqualPrincipalScheduleCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new EqualPrincipalScheduleCalculator();
    }

    @Test
    @DisplayName("Should calculate equal principal schedule with correct totals")
    void shouldCalculateEqualPrincipalScheduleWithCorrectTotals() {
        // Given: 1200 EUR loan, 12 months, 6% annual rate
        Loan loan = Loan.builder()
                .id(java.util.UUID.randomUUID())
                .loanType(LoanType.CONSUMER)
                .amount(new BigDecimal("1200.00"))
                .periodMonths(12)
                .annualInterestRate(new BigDecimal("6.00"))
                .scheduleType(ScheduleType.EQUAL_PRINCIPAL)
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
        assertThat(totalPrincipal).isEqualByComparingTo(new BigDecimal("1200.00"));
        
        // Total payments should equal principal + interest
        assertThat(totalPayments).isEqualByComparingTo(totalPrincipal.add(totalInterest));
        
        // Final balance should be zero
        ScheduleItem lastPayment = schedule.get(schedule.size() - 1);
        assertThat(lastPayment.getRemainingBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should have equal principal payments for all months")
    void shouldHaveEqualPrincipalPayments() {
        // Given
        Loan loan = Loan.builder()
                .id(java.util.UUID.randomUUID())
                .loanType(LoanType.CAR)
                .amount(new BigDecimal("10000.00"))
                .periodMonths(20)
                .annualInterestRate(new BigDecimal("5.50"))
                .scheduleType(ScheduleType.EQUAL_PRINCIPAL)
                .startDate(LocalDate.of(2024, 1, 1))
                .build();

        // When
        List<ScheduleItem> schedule = calculator.calculate(loan);

        // Then: All principal payments should be equal
        BigDecimal expectedPrincipal = new BigDecimal("500.00"); // 10000 / 20
        schedule.forEach(item -> 
            assertThat(item.getPrincipal()).isEqualByComparingTo(expectedPrincipal)
        );
    }

    @Test
    @DisplayName("Should have decreasing interest and decreasing total payment over time")
    void shouldHaveDecreasingInterestAndPayments() {
        // Given
        Loan loan = Loan.builder()
                .id(java.util.UUID.randomUUID())
                .loanType(LoanType.MORTGAGE)
                .amount(new BigDecimal("60000.00"))
                .periodMonths(30)
                .annualInterestRate(new BigDecimal("4.00"))
                .scheduleType(ScheduleType.EQUAL_PRINCIPAL)
                .startDate(LocalDate.of(2024, 1, 1))
                .build();

        // When
        List<ScheduleItem> schedule = calculator.calculate(loan);

        // Then: Interest should decrease over time (as balance decreases)
        for (int i = 0; i < schedule.size() - 1; i++) {
            assertThat(schedule.get(i).getInterest())
                    .isGreaterThan(schedule.get(i + 1).getInterest());
        }
        
        // Then: Total payment should decrease over time (interest decreases, principal stays same)
        for (int i = 0; i < schedule.size() - 1; i++) {
            assertThat(schedule.get(i).getPayment())
                    .isGreaterThan(schedule.get(i + 1).getPayment());
        }
        
        // Then: First payment should have highest interest
        ScheduleItem firstPayment = schedule.get(0);
        ScheduleItem lastPayment = schedule.get(schedule.size() - 1);
        assertThat(firstPayment.getInterest()).isGreaterThan(lastPayment.getInterest());
    }

    @Test
    @DisplayName("Should calculate schedule with correct payment dates")
    void shouldCalculateScheduleWithCorrectPaymentDates() {
        // Given
        LocalDate startDate = LocalDate.of(2024, 6, 10);
        Loan loan = Loan.builder()
                .id(java.util.UUID.randomUUID())
                .loanType(LoanType.CONSUMER)
                .amount(new BigDecimal("3000.00"))
                .periodMonths(6)
                .annualInterestRate(new BigDecimal("7.50"))
                .scheduleType(ScheduleType.EQUAL_PRINCIPAL)
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
    @DisplayName("Should have decreasing remaining balance over time")
    void shouldHaveDecreasingRemainingBalance() {
        // Given
        Loan loan = Loan.builder()
                .id(java.util.UUID.randomUUID())
                .loanType(LoanType.CAR)
                .amount(new BigDecimal("15000.00"))
                .periodMonths(15)
                .annualInterestRate(new BigDecimal("6.50"))
                .scheduleType(ScheduleType.EQUAL_PRINCIPAL)
                .startDate(LocalDate.of(2024, 1, 1))
                .build();

        // When
        List<ScheduleItem> schedule = calculator.calculate(loan);

        // Then: Remaining balance should decrease over time
        for (int i = 0; i < schedule.size() - 1; i++) {
            assertThat(schedule.get(i).getRemainingBalance())
                    .isGreaterThan(schedule.get(i + 1).getRemainingBalance());
        }
        
        // First payment should have initial loan amount as balance before
        // (we check that balance decreases by principal amount each month)
        BigDecimal principalPerMonth = schedule.get(0).getPrincipal();
        for (int i = 0; i < schedule.size() - 1; i++) {
            BigDecimal expectedNextBalance = schedule.get(i).getRemainingBalance()
                    .subtract(principalPerMonth);
            assertThat(schedule.get(i + 1).getRemainingBalance())
                    .isEqualByComparingTo(expectedNextBalance);
        }
    }
}
