package com.nicat.storebonus.repositories;

import com.nicat.storebonus.entities.MarketGradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketGradeHistoryRepository extends JpaRepository<MarketGradeHistory, Long> {
}