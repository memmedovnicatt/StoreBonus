package com.nicat.storebonus.repositories;

import com.nicat.storebonus.entities.Market;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketRepository extends JpaRepository<Market,Long> {
    List<Market> findAllByIsActiveTrue();
}
