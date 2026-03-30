package com.nicat.storebonus.services.impl;

import com.nicat.storebonus.dtos.request.GradeRuleRequest;
import com.nicat.storebonus.entities.Grade;
import com.nicat.storebonus.entities.GradeRule;
import com.nicat.storebonus.entities.Market;
import com.nicat.storebonus.entities.Position;
import com.nicat.storebonus.repositories.GradeRuleRepository;
import com.nicat.storebonus.services.GradeRuleService;
import com.nicat.storebonus.services.GradeService;
import com.nicat.storebonus.services.MarketService;
import com.nicat.storebonus.services.PositionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GradeRuleServiceImpl implements GradeRuleService {

    GradeRuleRepository gradeRuleRepository;
    GradeService gradeService;
    PositionService positionService;
    MarketService marketService;

    @Override
    public void create(GradeRuleRequest request) {
        log.info("Process started for Market ID: {}, Position ID: {}, Grade ID: {}",
                request.marketId(), request.positionId(), request.gradeId());
        Grade grade = gradeService.checkExistsGrade(request.gradeId());
        log.debug("Grade validated: {}", grade.getName());

        Position position = positionService.checkExistsPosition(request.positionId());
        log.debug("Position validated: {}", position.getName());

        Market market = marketService.checkExistsMarket(request.marketId());
        log.debug("Market validated: {}", market.getName());

        GradeRule gradeRule = GradeRule.builder()
                .bonusPercent(request.bonusPercent())
                .amount(request.amount())
                .market(market)
                .position(position)
                .grade(grade)
                .build();
        gradeRuleRepository.save(gradeRule);
        log.info("New rule created and saved");
    }
}
