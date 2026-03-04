package com.nicat.storebonus.dtos.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Date;

public record GradeCalculationRequest(
        @NotNull
        Long marketId,

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate
) {
}