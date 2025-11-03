package com.lhv.loanmanagement.schedule.service;

import com.lhv.loanmanagement.loan.Loan;
import com.lhv.loanmanagement.loan.enums.LoanType;
import com.lhv.loanmanagement.loan.enums.ScheduleType;
import com.lhv.loanmanagement.schedule.calculator.ScheduleCalculator;
import com.lhv.loanmanagement.schedule.model.ScheduleItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepaymentScheduleServiceTest {

    private RepaymentScheduleService service;
    
    @Mock
    private ScheduleCalculator annuityCalculator;
    
    @Mock
    private ScheduleCalculator equalPrincipalCalculator;

    @BeforeEach
    void setUp() {
        when(annuityCalculator.getScheduleType()).thenReturn(ScheduleType.ANNUITY);
        when(equalPrincipalCalculator.getScheduleType()).thenReturn(ScheduleType.EQUAL_PRINCIPAL);
        service = new RepaymentScheduleService(List.of(annuityCalculator, equalPrincipalCalculator));
    }

    @Test
    @DisplayName("Should calculate annuity schedule using correct calculator")
    void shouldCalculateAnnuityScheduleUsingCorrectCalculator() {
        Loan loan = createLoan(ScheduleType.ANNUITY);
        List<ScheduleItem> expectedSchedule = List.of(createScheduleItem(loan.getStartDate()));
        
        when(annuityCalculator.calculate(loan)).thenReturn(expectedSchedule);
        
        List<ScheduleItem> schedule = service.calculateSchedule(loan);
        
        assertThat(schedule).isEqualTo(expectedSchedule);
    }

    @Test
    @DisplayName("Should calculate equal principal schedule using correct calculator")
    void shouldCalculateEqualPrincipalScheduleUsingCorrectCalculator() {
        Loan loan = createLoan(ScheduleType.EQUAL_PRINCIPAL);
        List<ScheduleItem> expectedSchedule = List.of(createScheduleItem(loan.getStartDate()));
        
        when(equalPrincipalCalculator.calculate(loan)).thenReturn(expectedSchedule);
        
        List<ScheduleItem> schedule = service.calculateSchedule(loan);
        
        assertThat(schedule).isEqualTo(expectedSchedule);
    }

    @Test
    @DisplayName("Should delegate to calculateSchedule for annuity schedule")
    void shouldDelegateToCalculateScheduleForAnnuity() {
        Loan loan = createLoan(ScheduleType.ANNUITY);
        List<ScheduleItem> expectedSchedule = List.of(createScheduleItem(loan.getStartDate()));
        
        when(annuityCalculator.calculate(loan)).thenReturn(expectedSchedule);
        
        List<ScheduleItem> schedule = service.calculateAnnuitySchedule(loan);
        
        assertThat(schedule).isEqualTo(expectedSchedule);
    }

    @Test
    @DisplayName("Should delegate to calculateSchedule for equal principal schedule")
    void shouldDelegateToCalculateScheduleForEqualPrincipal() {
        Loan loan = createLoan(ScheduleType.EQUAL_PRINCIPAL);
        List<ScheduleItem> expectedSchedule = List.of(createScheduleItem(loan.getStartDate()));
        
        when(equalPrincipalCalculator.calculate(loan)).thenReturn(expectedSchedule);
        
        List<ScheduleItem> schedule = service.calculateEqualPrincipalSchedule(loan);
        
        assertThat(schedule).isEqualTo(expectedSchedule);
    }

    @Test
    @DisplayName("Should throw exception when loan is null")
    void shouldThrowExceptionWhenLoanIsNull() {
        assertThatThrownBy(() -> service.calculateSchedule(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when schedule type is null")
    void shouldThrowExceptionWhenScheduleTypeIsNull() {
        Loan loan = createLoan(ScheduleType.ANNUITY);
        loan.setScheduleType(null);
        
        assertThatThrownBy(() -> service.calculateSchedule(loan))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    @DisplayName("Should throw exception when service initialized with empty calculator list")
    void shouldThrowExceptionWhenServiceInitializedWithEmptyList() {
        assertThatThrownBy(() -> new RepaymentScheduleService(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should throw exception when calculator not found for schedule type")
    void shouldThrowExceptionWhenCalculatorNotFound() {
        ScheduleCalculator otherCalculator = org.mockito.Mockito.mock(ScheduleCalculator.class);
        when(otherCalculator.getScheduleType()).thenReturn(ScheduleType.ANNUITY);
        RepaymentScheduleService serviceWithSingleCalculator = new RepaymentScheduleService(List.of(otherCalculator));
        
        Loan loan = createLoan(ScheduleType.EQUAL_PRINCIPAL);
        
        assertThatThrownBy(() -> serviceWithSingleCalculator.calculateSchedule(loan))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported schedule type");
    }

    private Loan createLoan(ScheduleType scheduleType) {
        return Loan.builder()
                .id(UUID.randomUUID())
                .loanType(LoanType.CONSUMER)
                .amount(new BigDecimal("10000.00"))
                .periodMonths(24)
                .annualInterestRate(new BigDecimal("6.00"))
                .scheduleType(scheduleType)
                .startDate(LocalDate.of(2024, 1, 1))
                .build();
    }
    
    private ScheduleItem createScheduleItem(LocalDate paymentDate) {
        return ScheduleItem.builder()
                .paymentDate(paymentDate)
                .payment(new BigDecimal("500.00"))
                .principal(new BigDecimal("450.00"))
                .interest(new BigDecimal("50.00"))
                .remainingBalance(new BigDecimal("9550.00"))
                .build();
    }
}

