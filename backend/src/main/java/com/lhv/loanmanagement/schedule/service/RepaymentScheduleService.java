package com.lhv.loanmanagement.schedule.service;

import com.lhv.loanmanagement.loan.Loan;
import com.lhv.loanmanagement.loan.enums.ScheduleType;
import com.lhv.loanmanagement.schedule.calculator.ScheduleCalculator;
import com.lhv.loanmanagement.schedule.model.ScheduleItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RepaymentScheduleService {

    private final Map<ScheduleType, ScheduleCalculator> calculators;

    public RepaymentScheduleService(List<ScheduleCalculator> calculatorList) {
        Assert.notEmpty(calculatorList, "At least one schedule calculator must be provided");
        this.calculators = calculatorList.stream()
                .collect(Collectors.toMap(ScheduleCalculator::getScheduleType, calculator -> calculator));
        
        log.info("Initialized RepaymentScheduleService with {} calculators", calculators.size());
    }

    public List<ScheduleItem> calculateSchedule(Loan loan) {
        Assert.notNull(loan, "Loan cannot be null");
        Assert.notNull(loan.getScheduleType(), "Loan schedule type cannot be null");
        
        log.debug("Calculating schedule for loan id={}, scheduleType={}", 
                loan.getId(), loan.getScheduleType());
        
        ScheduleCalculator calculator = calculators.get(loan.getScheduleType());
        if (calculator == null) {
            log.error("Unsupported schedule type: {}", loan.getScheduleType());
            throw new IllegalArgumentException("Unsupported schedule type: " + loan.getScheduleType());
        }
        
        List<ScheduleItem> schedule = calculator.calculate(loan);
        log.debug("Calculated schedule with {} items for loan id={}", schedule.size(), loan.getId());
        
        return schedule;
    }

    public List<ScheduleItem> calculateAnnuitySchedule(Loan loan) {
        Assert.notNull(loan, "Loan cannot be null");
        return calculateSchedule(loan);
    }

    public List<ScheduleItem> calculateEqualPrincipalSchedule(Loan loan) {
        Assert.notNull(loan, "Loan cannot be null");
        return calculateSchedule(loan);
    }
}

