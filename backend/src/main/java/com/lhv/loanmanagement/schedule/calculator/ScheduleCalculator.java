package com.lhv.loanmanagement.schedule.calculator;

import com.lhv.loanmanagement.loan.Loan;
import com.lhv.loanmanagement.loan.enums.ScheduleType;
import com.lhv.loanmanagement.schedule.model.ScheduleItem;

import java.util.List;

public interface ScheduleCalculator {
    List<ScheduleItem> calculate(Loan loan);
    
    ScheduleType getScheduleType();
}

