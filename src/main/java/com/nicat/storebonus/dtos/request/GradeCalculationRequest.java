package com.nicat.storebonus.dtos.request;

import jakarta.validation.constraints.NotNull;

public record GradeCalculationRequest(
        @NotNull
        Long marketId
) {
}