package com.nicat.storebonus.services;

import com.nicat.storebonus.dtos.request.MarketRequest;
import com.nicat.storebonus.entities.Market;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MarketService {
    void create(@Valid MarketRequest marketRequest);

    Market checkExistsMarket(Long marketId);

    List<Market> getAll();

    void delete(Long id);
}