package com.lhv.loanmanagement.loan.dto;

import com.lhv.loanmanagement.loan.model.ScheduleItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleResponse {

    private List<ScheduleItem> items;
}

