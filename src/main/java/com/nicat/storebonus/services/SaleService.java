package com.nicat.storebonus.services;

import com.nicat.storebonus.dtos.request.SaleRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface SaleService {
    void create(@Valid SaleRequest saleRequest);
}
