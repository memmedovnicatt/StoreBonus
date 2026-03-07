package com.nicat.storebonus.dtos.response;

import java.math.BigDecimal;

public record GradeRuleResponse(
        Long gradeId,
        Long positionId,
        Long employeeId,
        Long marketId,
        Double bonusPercent,
        BigDecimal bonusAmount,
        String currency
) {
}