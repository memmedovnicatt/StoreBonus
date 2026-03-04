package com.nicat.storebonus.repositories;

import com.nicat.storebonus.entities.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
}
