package com.nicat.storebonus.dtos.response;

import java.math.BigDecimal;

public record EmployeeGradeItem(
        Long gradeId,
        Long positionId,
        Long marketId,
        Double bonusPercent,
        BigDecimal amount
) {
}