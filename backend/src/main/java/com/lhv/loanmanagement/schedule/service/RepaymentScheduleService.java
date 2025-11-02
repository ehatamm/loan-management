package com.lhv.loanmanagement.schedule.service;

import com.lhv.loanmanagement.loan.Loan;
import com.lhv.loanmanagement.loan.enums.ScheduleType;
import com.lhv.loanmanagement.schedule.calculator.AnnuityScheduleCalculator;
import com.lhv.loanmanagement.schedule.calculator.EqualPrincipalScheduleCalculator;
import com.lhv.loanmanagement.schedule.calculator.ScheduleCalculator;
import com.lhv.loanmanagement.schedule.model.ScheduleItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RepaymentScheduleService {

    private final Map<ScheduleType, ScheduleCalculator> calculators;

    public RepaymentScheduleService(List<ScheduleCalculator> calculatorList) {
        this.calculators = calculatorList.stream()
                .collect(Collectors.toMap(calculator -> getScheduleType(calculator), Function.identity()));
    }

    public List<ScheduleItem> calculateSchedule(Loan loan) {
        ScheduleCalculator calculator = calculators.get(loan.getScheduleType());
        if (calculator == null) {
            throw new IllegalArgumentException("Unsupported schedule type: " + loan.getScheduleType());
        }
        return calculator.calculate(loan);
    }

    public List<ScheduleItem> calculateAnnuitySchedule(Loan loan) {
        return calculators.get(ScheduleType.ANNUITY).calculate(loan);
    }

    public List<ScheduleItem> calculateEqualPrincipalSchedule(Loan loan) {
        return calculators.get(ScheduleType.EQUAL_PRINCIPAL).calculate(loan);
    }

    private ScheduleType getScheduleType(ScheduleCalculator calculator) {
        if (calculator instanceof AnnuityScheduleCalculator) {
            return ScheduleType.ANNUITY;
        } else if (calculator instanceof EqualPrincipalScheduleCalculator) {
            return ScheduleType.EQUAL_PRINCIPAL;
        } else {
            throw new IllegalArgumentException("Unknown calculator type: " + calculator.getClass());
        }
    }
}

