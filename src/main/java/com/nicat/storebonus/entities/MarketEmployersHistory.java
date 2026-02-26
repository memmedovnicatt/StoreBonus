package com.nicat.storebonus.entities;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "market_employers_history")
@EntityListeners(AuditingEntityListener.class)
public class MarketEmployersHistory extends BaseEntity {

    @ManyToOne()
    @JoinColumn(name = "employer_id")
    Employer employer;

    @ManyToOne()
    @JoinColumn(name = "market_id")
    Market market;
}