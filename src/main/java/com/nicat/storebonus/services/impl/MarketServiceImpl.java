package com.nicat.storebonus.services.impl;

import com.nicat.storebonus.dtos.request.MarketRequest;
import com.nicat.storebonus.entities.Grade;
import com.nicat.storebonus.entities.Market;
import com.nicat.storebonus.entities.MarketGradeHistory;
import com.nicat.storebonus.entities.WareHouse;
import com.nicat.storebonus.exceptions.handler.ResourceNotFoundException;
import com.nicat.storebonus.repositories.MarketGradeHistoryRepository;
import com.nicat.storebonus.repositories.MarketRepository;
import com.nicat.storebonus.services.GradeService;
import com.nicat.storebonus.services.MarketService;
import com.nicat.storebonus.services.WareHouseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.events.Event;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MarketServiceImpl implements MarketService {

    MarketRepository marketRepository;
    WareHouseService wareHouseService;
    GradeService gradeService;
    MarketGradeHistoryRepository marketGradeHistoryRepository;


    //    @Transactional
    @Override
    public void create(MarketRequest marketRequest) {
        log.info("Create starting for Market");
        WareHouse wareHouse = wareHouseService.checkExistsWareHouse(marketRequest.wareHouseId());
        log.debug("WareHouse validated: {}", wareHouse.getName());

        Grade grade = null;
        if (marketRequest.gradeId() != null) {
            grade = gradeService.checkExistsGrade(marketRequest.gradeId());
        }

        BigDecimal middleThreshold = calculateMiddleThreshold(marketRequest.minThreshold(),
                marketRequest.maxThreshold());
        log.info("Calculate middle threshold for min and max value of threshold:{}", middleThreshold);

        Market market = Market.builder()
                .wareHouse(wareHouse)
                .name(marketRequest.name())
                .location(marketRequest.location())
                .build();
        marketRepository.save(market);
        log.info("Market was created and saved");

        MarketGradeHistory marketGradeHistory = MarketGradeHistory.builder()
                .market(market)
                .grade(grade)
                .startDate(LocalDate.now())
                .minThreshold(marketRequest.minThreshold())
                .maxThreshold(marketRequest.maxThreshold())
                .build();
        marketGradeHistoryRepository.save(marketGradeHistory);
        log.info("MarketGradeHistory was created and saved");
    }

    @Override
    public Market checkExistsMarket(Long marketId) {
        return marketRepository.findById(marketId)
                .orElseThrow(() -> new ResourceNotFoundException("Market", "id", marketId));
    }

    @Override
    public List<Market> getAll() {
        return marketRepository.findAllByIsActiveTrue();
    }

    @Override
    public void delete(Long id) {
        log.info("Delete was started with Market ID:{}", id);
        Market market = marketRepository.findById(id).orElse(null);
        if (market == null) {
            log.warn("Market was not found with ID:{}", id);
            throw new ResourceNotFoundException("Market", "id", id);
        }
        market.setActive(false);
        market.setDeletedAt(LocalDateTime.now());
        marketRepository.save(market);
        log.info("Market was saved");
    }

    public BigDecimal calculateMiddleThreshold(BigDecimal a, BigDecimal b) {
        return a.add(b).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    }
}