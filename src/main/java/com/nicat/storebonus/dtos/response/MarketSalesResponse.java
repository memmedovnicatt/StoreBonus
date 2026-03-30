package com.nicat.storebonus.dtos.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MarketSalesResponse {
    private String employeeName;
    private String employeeSurname;
    private String marketName;
    private LocalDate date;
    private BigDecimal price;
    private String currency;
}