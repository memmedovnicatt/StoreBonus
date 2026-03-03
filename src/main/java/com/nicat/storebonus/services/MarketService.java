package com.nicat.storebonus.services;

import com.nicat.storebonus.dtos.request.MarketRequest;
import com.nicat.storebonus.entities.Market;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public interface MarketService {
    void create(@Valid MarketRequest marketRequest);

    Market checkExistsMarket(Long marketId);
}
