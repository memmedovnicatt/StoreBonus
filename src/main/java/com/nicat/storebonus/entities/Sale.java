package com.nicat.storebonus.entities;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "sales")
public class Sale extends BaseEntity {
    //employer_id
    @ManyToOne
    @JoinColumn(name = "employee_id")
    Employee employee;

    //market_id
    @ManyToOne
    @JoinColumn(name = "market_id")
    Market market;

    LocalDate date;
    BigDecimal price;
    String currency;
}