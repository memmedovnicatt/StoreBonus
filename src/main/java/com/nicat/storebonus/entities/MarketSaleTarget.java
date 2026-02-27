package com.nicat.storebonus.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "employer_sale_targets")
public class MarketSaleTarget extends BaseEntity {

    //market_id
    @ManyToOne
    @JoinColumn(name = "market_id")
    Market market;

    String period;
    Double targetAmount;
}
