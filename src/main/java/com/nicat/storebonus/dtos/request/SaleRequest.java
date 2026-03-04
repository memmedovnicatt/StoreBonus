package com.nicat.storebonus.dtos.request;

import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SaleRequest(
        Long employerId,

        Long marketId,

        LocalDate date,

        @Min(0)
        BigDecimal price,

        String currency
) {
}
