package com.nicat.storebonus.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
public class EmployeeContractResponse {
    Long gradeId;
    Long positionId;
    Long marketId;
    Long employeeId;
    BigDecimal baseSalary;
    BigDecimal bonusAmount;
    BigDecimal totalAmount;
    String currency;
    LocalDate validFrom;
    LocalDate validTo;
}