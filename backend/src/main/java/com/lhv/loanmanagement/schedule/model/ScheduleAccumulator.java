package com.lhv.loanmanagement.schedule.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ScheduleAccumulator {
    private BigDecimal balance;
    private BigDecimal accumulatedPrincipal;
    private final List<ScheduleItem> items;
    
    public ScheduleAccumulator(BigDecimal balance) {
        this.balance = balance;
        this.accumulatedPrincipal = BigDecimal.ZERO;
        this.items = new ArrayList<>();
    }
    
    public void addItem(ScheduleItem item, BigDecimal newBalance) {
        this.items.add(item);
        this.balance = newBalance;
        // Track sum of rounded principals for exact totals (industry standard)
        this.accumulatedPrincipal = this.accumulatedPrincipal.add(item.getPrincipal());
    }
}

