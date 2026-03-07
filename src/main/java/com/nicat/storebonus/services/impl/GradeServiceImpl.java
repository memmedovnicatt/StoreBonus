package com.nicat.storebonus.services.impl;

import com.nicat.storebonus.dtos.request.GradeCalculationRequest;
import com.nicat.storebonus.dtos.request.GradeRequest;
import com.nicat.storebonus.dtos.response.EmployerContractResponse;
import com.nicat.storebonus.dtos.response.GradeRuleResponse;
import com.nicat.storebonus.entities.*;
import com.nicat.storebonus.enums.GradeType;
import com.nicat.storebonus.exceptions.handler.ResourceNotFoundException;
import com.nicat.storebonus.exceptions.handler.TargetNotReachedException;
import com.nicat.storebonus.repositories.*;
import com.nicat.storebonus.services.GradeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GradeServiceImpl implements GradeService {

    GradeRepository gradeRepository;
    SaleRepository saleRepository;
    MarketGradeHistoryRepository marketGradeHistoryRepository;
    GradeRuleRepository gradeRuleRepository;
    EmployerContractRepository employerContractRepository;
    EmployerRepository employerRepository;
    GradeHistoryRepository gradeHistoryRepository;

    @Override
    public void create(GradeRequest gradeRequest) {
        Grade grade = Grade.builder()
                .gradeType(gradeRequest.gradeType())
                .name(gradeRequest.name())
                .build();
        gradeRepository.save(grade);
    }

    @Override
    public Grade checkExistsGrade(Long gradeId) {
        return gradeRepository.findById(gradeId)
                .orElseThrow(() -> new ResourceNotFoundException("Grade", "id", gradeId));
    }

    @Override
    public void calculateGrade(GradeCalculationRequest gradeCalculationRequest) {
        StopWatch watch = new StopWatch();
        watch.start();

        //check active grade of market
        Optional<MarketGradeHistory> optional = marketGradeHistoryRepository
                .findByMarketIdAndIsActive(gradeCalculationRequest.marketId(), true);

        //check for if active grade of market is not present,code not throws exception
        if (optional.isPresent()) {
            MarketGradeHistory marketGradeHistory = optional.get();

            log.info("{}", marketGradeHistory.getGrade().getGradeType());

            //show that external method,because all types of grade utilized it
            BigDecimal totalSale = calculateSalesOfMarket(marketGradeHistory.getMarket().getId(),
                    marketGradeHistory.getStartDate());

            Long gradeId = marketGradeHistory.getGrade().getId();
            log.info("gradeId: {}", gradeId);

            Long marketId = marketGradeHistory.getMarket().getId();
            log.info("marketId: {}", marketId);

            //select bonus amount of active positions
            List<GradeRuleResponse> gradeRules = gradeRuleRepository
                    .findByGradeIdAndMarketId(gradeId,
                            marketId);
            log.info("gradeRuleResponse : {} ", gradeRules);


            if (marketGradeHistory.getGrade().getGradeType() == GradeType.Fixed) {

                log.info("total sale price of market :{}", totalSale);

                if (totalSale.compareTo(marketGradeHistory.getMinThreshold()) <= 0) {
                    throw new TargetNotReachedException("Market", "min threshold",
                            marketGradeHistory.getMinThreshold());
                }


                List<Long> employeeIds = gradeRules.stream()
                        .map(GradeRuleResponse::employeeId)
                        .distinct()
                        .toList();

                log.info("employeeIds : {}", employeeIds);

                List<EmployerContract> contracts = employerContractRepository
                        .findAllByEmployerIdInAndIsActive(employeeIds, true);

                Map<Long, EmployerContract> contractMap = contracts.stream()
                        .collect(Collectors.toMap(c -> c.getEmployer().getId(), Function.identity()));

                //create list for set all employers bonus amount with base salary
                List<EmployerContractResponse> employerContractResponse = new ArrayList<>();

                for (GradeRuleResponse gradeRuleResponse : gradeRules) {
                    EmployerContract contract = contractMap.get(gradeRuleResponse.employeeId());

                    log.info("contract: {} ", contract);

                    if (contract != null) {
                        EmployerContractResponse response = new EmployerContractResponse();

                        response.setGradeId(gradeRuleResponse.gradeId());
                        response.setPositionId(contract.getPosition().getId());
                        response.setMarketId(contract.getMarket().getId());
                        response.setEmployerId(contract.getEmployer().getId());
                        response.setBaseSalary(contract.getBaseSalary());
                        response.setBonusAmount(gradeRuleResponse.bonusAmount());
                        response.setCurrency(contract.getCurrency());
                        response.setValidFrom(contract.getValidFrom());
                        response.setValidTo(contract.getValidTo());

                        BigDecimal baseSalary = Optional.ofNullable(contract.getBaseSalary()).orElse(BigDecimal.ZERO);
                        BigDecimal bonusAmount = Optional.ofNullable(gradeRuleResponse.bonusAmount()).orElse(BigDecimal.ZERO);
                        response.setTotalAmount(baseSalary.add(bonusAmount));

                        employerContractResponse.add(response);
                    }
                }

                log.info("employerContract response : {}", employerContractResponse);

                List<Long> employerIds = employerContractResponse.stream()
                        .map(EmployerContractResponse::getEmployerId)
                        .distinct()
                        .toList();

                Map<Long, Employer> employerMap = employerRepository.findAllById(employerIds)
                        .stream()
                        .collect(Collectors.toMap(Employer::getId, e -> e));

                List<GradeHistory> gradeHistories = new ArrayList<>();


                for (EmployerContractResponse response : employerContractResponse) {
                    Employer employer = employerMap.get(response.getEmployerId());

                    GradeHistory gradeHistory = new GradeHistory();
                    gradeHistory.setEmployer(employer);
                    gradeHistory.setBaseSalary(response.getBaseSalary());
                    gradeHistory.setBonusAmount(response.getBonusAmount());
                    gradeHistory.setTotalSalary(response.getTotalAmount());
                    gradeHistory.setPaidAt(LocalDateTime.now());
                    gradeHistory.setPeriod("MONTHLY");

                    gradeHistories.add(gradeHistory);
                }
                gradeHistoryRepository.saveAll(gradeHistories);
            }

        }
        watch.stop();
        log.info("Calculate Grade method execution time: {} ms", watch.getTotalTimeMillis());
    }


    public BigDecimal calculateSalesOfMarket(Long marketId, LocalDate startDate) {
        return saleRepository.sumPriceByMarketIdAndDate(marketId,
                startDate);
    }
//    public
}