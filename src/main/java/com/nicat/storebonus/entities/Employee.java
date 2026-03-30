package com.nicat.storebonus.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "employees")
public class Employee extends BaseEntity {
    String name;
    String surname;
    String mail;
    String phoneNumber;
    byte age;

    LocalDateTime deletedAt;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "position_id")
    Position position;
}