package com.nicat.storebonus.dtos.request;

import java.math.BigDecimal;

public record GradeRuleRequest(
        Long gradeId,
        Long positionId,
        Long marketId,
        Double bonusPercent,
        BigDecimal amount
) {
}