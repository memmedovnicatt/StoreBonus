package com.nicat.storebonus.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class GradeRuleResponse {
    private Long gradeId;
    private Long positionId;
    private Long employeeId;
    private Long marketId;
    private BigDecimal bonusPercent;
    private BigDecimal bonusAmount;
    private String currency;
}