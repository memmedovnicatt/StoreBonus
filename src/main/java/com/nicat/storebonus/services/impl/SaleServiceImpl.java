package com.nicat.storebonus.services.impl;

import com.nicat.storebonus.dtos.request.SaleRequest;
import com.nicat.storebonus.entities.Employer;
import com.nicat.storebonus.entities.Market;
import com.nicat.storebonus.entities.Sale;
import com.nicat.storebonus.mapper.SaleMapper;
import com.nicat.storebonus.repositories.SaleRepository;
import com.nicat.storebonus.services.EmployerService;
import com.nicat.storebonus.services.MarketService;
import com.nicat.storebonus.services.SaleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SaleServiceImpl implements SaleService {

    SaleRepository saleRepository;
    SaleMapper saleMapper;
    EmployerService employerService;
    MarketService marketService;

    @Override
    public void create(SaleRequest saleRequest) {
        Market market = marketService.checkExistsMarket(saleRequest.marketId());

        Employer employer = employerService.checkExistsEmployer(saleRequest.employerId());

        Sale savedSale = saleMapper.toSale(saleRequest);
        savedSale.setEmployer(employer);
        savedSale.setMarket(market);

        saleRepository.save(savedSale);
    }
}
