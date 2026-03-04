package com.nicat.storebonus.dtos.request;

import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.util.Date;

public record SaleRequest(
        Long employerId,

        Long marketId,

        Date date,

        @Min(0)
        BigDecimal price,

        String currency
) {
}
