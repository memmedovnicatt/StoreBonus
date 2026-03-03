package com.nicat.storebonus.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "market_grade_histories")
public class MarketGradeHistory extends BaseEntity {

    LocalDate startDate;
    LocalDate endDate;

    //grade_id
    @ManyToOne
    @JoinColumn(name = "grade_id")
    Grade grade;

    //market_id
    @ManyToOne
    @JoinColumn(name = "market_id")
    Market market;

}