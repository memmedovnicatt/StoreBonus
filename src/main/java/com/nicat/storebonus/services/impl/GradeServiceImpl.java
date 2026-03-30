package com.nicat.storebonus.services.impl;

import com.nicat.storebonus.dtos.request.GradeCalculationRequest;
import com.nicat.storebonus.dtos.request.GradeRequest;
import com.nicat.storebonus.dtos.response.EmployeeContractResponse;
import com.nicat.storebonus.dtos.response.MarketGradeHistoryResponse;
import com.nicat.storebonus.dtos.response.GradeRuleResponse;
import com.nicat.storebonus.entities.*;
import com.nicat.storebonus.exceptions.handler.ResourceNotFoundException;
import com.nicat.storebonus.exceptions.handler.TargetNotReachedException;
import com.nicat.storebonus.mapper.GradeHistoryMapper;
import com.nicat.storebonus.mapper.GradeMapper;
import com.nicat.storebonus.repositories.*;
import com.nicat.storebonus.services.GradeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
    EmployeeContractRepository employeeContractRepository;
    EmployeeRepository employeeRepository;
    GradeHistoryRepository gradeHistoryRepository;
    GradeHistoryMapper gradeHistoryMapper;
    MarketRepository marketRepository;
    GradeMapper gradeMapper;

    @Override
    public void create(GradeRequest gradeRequest) {
        Grade grade = Grade.builder()
                .gradeType(gradeRequest.gradeType())
                .name(gradeRequest.name())
                .generalPercent(gradeRequest.generalPercent())
                .minPercent(gradeRequest.minPercent())
                .maxPercent(gradeRequest.maxPercent())
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
        log.info("calculateGrade method was started with marketId: {}",
                gradeCalculationRequest.marketId());

        Optional<MarketGradeHistory> optional =
                checkActiveGradeOfMarket(gradeCalculationRequest.marketId(), true);

        //check for if active grade of market is not present,code not throws exception
        if (optional.isPresent()) {
            MarketGradeHistory marketGradeHistory = optional.get();

            if (marketGradeHistory.getGrade() == null) {
                throw new ResourceNotFoundException("Grade", "id", marketGradeHistory.getGrade());
            }

            //show that external method,because all types of grade utilized it
            BigDecimal totalSale = calculateSalesOfMarket(marketGradeHistory.getMarket().getId(),
                    marketGradeHistory.getStartDate());
            log.debug("Market's total sale is :{}", totalSale);

            //compare total sale compare to null and zero
            validateTotalSale(totalSale);

            //check type of grade and execute it
            processGradeType(marketGradeHistory, totalSale);

        } else {
            log.error("Calculation failed: No active grade found | marketId: {}", gradeCalculationRequest.marketId());
            throw new ResourceNotFoundException("Grade of market", "id", gradeCalculationRequest.marketId());
        }
        watch.stop();
        log.info("Calculate Grade method execution time: {} ms", watch.getTotalTimeMillis());
    }

    private void validateTotalSale(BigDecimal totalSale) {
        //check total sales of market,if conditions are true,the code will not be executed
        if (totalSale == null || totalSale.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Calculation aborted: Total sales are zero or null.");
            throw new TargetNotReachedException("Market", "sales", BigDecimal.ZERO);
        }
    }

    private void handlePercentGrade(MarketGradeHistory marketGradeHistory, BigDecimal totalSale) {
        //select bonus amount of active positions
        //todo:maybe extract general method - > getRules()
        List<GradeRuleResponse> rules = gradeRuleRepository
                .findByGradeIdAndMarketId(marketGradeHistory.getGrade().getId(),
                        marketGradeHistory.getMarket().getId());

        List<Long> employeeIds = collectEmployeeIds(rules);

        int totalStaff = employeeContractRepository
                .countByMarketIdAndIsActive(marketGradeHistory.getMarket().getId(), true);

        BigDecimal fixAmount = totalSale.multiply(
                marketGradeHistory.getGrade().getGeneralPercent().divide(BigDecimal.valueOf(100),
                        4, RoundingMode.HALF_UP));

        List<EmployeeContractResponse> specialResponses = calculateSpecialPercentBonus(rules, fixAmount, getContractMap(employeeIds));

        BigDecimal distributedPercent = rules.stream()
                .map(GradeRuleResponse::getBonusPercent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<EmployeeContractResponse> nonSpecialResponses =
                calculateNonSpecialPercentBonus(marketGradeHistory, fixAmount, distributedPercent, employeeIds, totalStaff);

        List<EmployeeContractResponse> allResponses = new ArrayList<>(specialResponses);
        allResponses.addAll(nonSpecialResponses);

        saveGradeHistories(allResponses, getEmployerMap(allResponses.stream().map(EmployeeContractResponse::getEmployeeId).toList()), marketGradeHistory.getMarket().getId());
    }

    private void processGradeType(MarketGradeHistory marketGradeHistory, BigDecimal totalSale) {
        log.debug("Type of grade is : {}", marketGradeHistory.getGrade().getGradeType());
        switch (marketGradeHistory.getGrade().getGradeType()) {
            case Fixed -> handleFixedGrade(marketGradeHistory);
            case Threshold -> handleThresholdGrade(marketGradeHistory, totalSale);
            case Percent -> handlePercentGrade(marketGradeHistory, totalSale);
        }
    }


    private List<EmployeeContractResponse> calculateSpecialPercentBonus(List<GradeRuleResponse> rules, BigDecimal pool, Map<Long, EmployeeContract> contractMap) {
        return rules.stream().map(rule -> {
            BigDecimal individualBonus = pool.multiply(rule.getBonusPercent()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            return mapToResponse(contractMap.get(rule.getEmployeeId()), individualBonus, rule.getGradeId());
        }).toList();
    }

    private List<EmployeeContractResponse> calculateNonSpecialPercentBonus(MarketGradeHistory history, BigDecimal pool, BigDecimal usedPercent, List<Long> excludedIds, int totalStaff) {
        int remainingCount = totalStaff - excludedIds.size();
        if (remainingCount <= 0) return Collections.emptyList();

        BigDecimal remainingPercent = BigDecimal.valueOf(100).subtract(usedPercent);
        BigDecimal remainingPool = pool.multiply(remainingPercent.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        BigDecimal amountPerPerson = remainingPool.divide(BigDecimal.valueOf(remainingCount), 2, RoundingMode.HALF_UP);

        return employeeContractRepository.findByEmployeeIdNotIn(excludedIds, history.getMarket().getId(), true)
                .stream()
                .peek(res -> {
                    res.setBonusAmount(amountPerPerson);
                    res.setTotalAmount(res.getBaseSalary().add(amountPerPerson));
                }).toList();
    }


    private void handleFixedGrade(MarketGradeHistory marketGradeHistory) {
        //select bonus amount of active positions
        //todo:maybe extract general method - > getRules()
        List<GradeRuleResponse> rules = gradeRuleRepository
                .findByGradeIdAndMarketId(marketGradeHistory.getGrade().getId(),
                        marketGradeHistory.getMarket().getId());

        //select employeeIds from rules
        List<Long> employeeIds = collectEmployeeIds(rules);

        //getEmployees together ID
        Map<Long, Employee> employerMap = getEmployerMap(employeeIds);

        //getContracts of employees which belong to rules
        Map<Long, EmployeeContract> contractMap = getContractMap(employeeIds);

        List<EmployeeContractResponse> employeeContractRespons = rules.stream()
                .map(rule -> mapToResponse(contractMap.get(rule.getEmployeeId()), rule.getBonusAmount(), rule.getGradeId()))
                .toList();

        log.debug("EmployeeContractResponses : {}", employeeContractRespons);

        //extract method,because all methods used this.
        saveGradeHistories(employeeContractRespons, employerMap, marketGradeHistory.getMarket().getId());

        log.debug("Grades was successfully saved in GradeHistory");
    }

    private Map<Long, Employee> getEmployerMap(List<Long> ids) {
        Map<Long, Employee> employerMap = employeeRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Employee::getId, e -> e));
        log.debug("EmployeeMap : {}", employerMap);
        return employerMap;
    }


    private Map<Long, EmployeeContract> getContractMap(List<Long> ids) {
        Map<Long, EmployeeContract> employerContractMap = employeeContractRepository
                .findAllByEmployeeIdInAndIsActive(ids, true).stream()
                .collect(Collectors.toMap(c -> c.getEmployee().getId(), Function.identity()));
        log.debug("EmployerContract : {}", employerContractMap);
        return employerContractMap;
    }

    private void handleThresholdGrade(MarketGradeHistory marketGradeHistory, BigDecimal totalSale) {
        log.info("handleThresholdGrade method was started");
        List<EmployeeContractResponse> employeeContractRespons = new ArrayList<>();

        int totalCountOfEmployer = employeeContractRepository
                .countByMarketIdAndIsActive(marketGradeHistory.getMarket().getId(), true);

        //calculate middle threshold
        BigDecimal middleThreshold = calculateMiddleThreshold(marketGradeHistory.getMinThreshold(),
                marketGradeHistory.getMaxThreshold());

        //calculate percent
        BigDecimal percent = calculateThresholdPercent(totalSale,
                marketGradeHistory, middleThreshold);

        //calculate new amount with new percent
        BigDecimal fixAmount = totalSale.multiply(
                percent.divide(BigDecimal.valueOf(100),
                        4, RoundingMode.HALF_UP));

        //calculate bonus amount for each employee
        BigDecimal bonusAmountPerEmployee = fixAmount.divide(
                BigDecimal.valueOf(totalCountOfEmployer),
                2,
                RoundingMode.HALF_UP
        );

        List<EmployeeContractResponse> employerContracts = employeeContractRepository
                .findAllByMarketIdAndIsActive(marketGradeHistory.getMarket().getId(), true);

        //todo:maybe change to stream api format line 163-182,but my opinion this format more readable
        for (EmployeeContractResponse updateContractResponse : employerContracts) {
            BigDecimal baseSalary = Optional.ofNullable(updateContractResponse.getBaseSalary()).orElse(BigDecimal.ZERO);

            updateContractResponse.setBonusAmount(bonusAmountPerEmployee);

            updateContractResponse.setTotalAmount(baseSalary.add(bonusAmountPerEmployee));

            employeeContractRespons.add(updateContractResponse);
        }

        List<Long> employeeIds = employeeContractRespons.stream()
                .map(EmployeeContractResponse::getEmployeeId)
                .distinct()
                .toList();

        Map<Long, Employee> employerMap = employeeRepository.findAllById(employeeIds)
                .stream()
                .collect(Collectors.toMap(Employee::getId, e -> e));

        saveGradeHistories(employeeContractRespons, employerMap, marketGradeHistory.getMarket().getId());
    }

    public EmployeeContractResponse mapToResponse(EmployeeContract contract, BigDecimal bonus, Long gradeId) {
        BigDecimal baseSalary = Optional.ofNullable(contract.getBaseSalary())
                .orElse(BigDecimal.ZERO);
        return EmployeeContractResponse.builder()
                .totalAmount(baseSalary.add(bonus))
                .baseSalary(baseSalary)
                .employeeId(contract.getEmployee().getId())
                .gradeId(gradeId)
                .positionId(contract.getPosition().getId())
                .marketId(contract.getMarket().getId())
                .bonusAmount(bonus)
                .currency(contract.getCurrency())
                .validFrom(contract.getValidFrom())
                .validTo(contract.getValidTo())
                .build();
    }

    private BigDecimal calculateThresholdPercent(BigDecimal totalSale, MarketGradeHistory marketGradeHistory, BigDecimal middleThreshold) {
        log.info("calculateThresholdPercent method was started"); //todo: AOP logging
        BigDecimal percent = BigDecimal.ZERO;
        if (totalSale.compareTo(marketGradeHistory.getMinThreshold()) >= 0 && totalSale.compareTo(middleThreshold) <= 0) {
            percent = marketGradeHistory.getGrade().getMinPercent();
        } else if (totalSale.compareTo(middleThreshold) > 0 && totalSale.compareTo(marketGradeHistory.getMaxThreshold()) <= 0) {
            percent = middleThreshold;
        } else if (totalSale.compareTo(marketGradeHistory.getMaxThreshold()) > 0) {
            percent = marketGradeHistory.getGrade().getMaxPercent();
        }
        return percent;

    }

    public BigDecimal calculateMiddleThreshold(BigDecimal a, BigDecimal b) {
        return a.add(b).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
    }

    public List<Long> collectEmployeeIds(List<GradeRuleResponse> gradeRules) {
        return gradeRules.stream()
                .map(GradeRuleResponse::getEmployeeId)
                .distinct()
                .toList();
    }

    private BigDecimal calculateSalesOfMarket(Long marketId, LocalDate startDate) {
        return saleRepository.sumPriceByMarketIdAndDate(marketId,
                startDate);
    }

    private void saveGradeHistories(List<EmployeeContractResponse> responses, Map<Long, Employee> employerMap, Long marketId) {
        Market currentMarket = marketRepository.getReferenceById(marketId);
        List<GradeHistory> histories = responses.stream().map(res -> {
            GradeHistory history = new GradeHistory();
            history.setEmployee(employerMap.get(res.getEmployeeId()));
            history.setBaseSalary(res.getBaseSalary());
            history.setBonusAmount(res.getBonusAmount());
            history.setMarket(currentMarket);
            history.setTotalSalary(res.getTotalAmount());
            history.setPaidAt(LocalDateTime.now());
            history.setPeriod("MONTHLY");//
            return history;
        }).toList();
        gradeHistoryRepository.saveAll(histories);
        log.debug("All grades were saved");
    }

    public Optional<MarketGradeHistory> checkActiveGradeOfMarket(Long marketId, boolean isActive) {
        return marketGradeHistoryRepository.findByMarketIdAndIsActive(marketId, isActive);
    }

    @Override
    public List<MarketGradeHistoryResponse> getAll() {
        log.debug("getAll method was started for GradeService");
        List<GradeHistory> gradeHistories = gradeHistoryRepository.findAll();

        log.debug("gradeHistories :{} ", gradeHistories.size());

        return gradeHistoryMapper.toListGradeHistory(gradeHistories);
    }

    @Override
    public void update(Long id, GradeRequest gradeRequest) {
        Grade grade = gradeRepository.findById(id).orElse(null);
        if (grade == null) {
            throw new ResourceNotFoundException("Grade", "id", id);
        }

        gradeMapper.updateEntityForFields(gradeRequest, grade);
        grade.setUpdatedAt(LocalDateTime.now());
        gradeRepository.save(grade);
    }
}