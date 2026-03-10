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
    EmployerContractRepository employerContractRepository;
    EmployerRepository employerRepository;
    GradeHistoryRepository gradeHistoryRepository;

    @Override
    public void create(GradeRequest gradeRequest) {
        Grade grade = Grade.builder()
                .gradeType(gradeRequest.gradeType())
                .name(gradeRequest.name())
                .generalPercent(gradeRequest.generalPercent())
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

            //show that external method,because all types of grade utilized it
            BigDecimal totalSale = calculateSalesOfMarket(marketGradeHistory.getMarket().getId(),
                    marketGradeHistory.getStartDate());

            Long gradeId = marketGradeHistory.getGrade().getId();

            Long marketId = marketGradeHistory.getMarket().getId();

            //select bonus amount of active positions
            List<GradeRuleResponse> gradeRules = gradeRuleRepository
                    .findByGradeIdAndMarketId(gradeId,
                            marketId);

            List<Long> employeeIds = gradeRules.stream()
                    .map(GradeRuleResponse::getEmployeeId)
                    .distinct()
                    .toList();

            List<EmployerContract> contracts = employerContractRepository
                    .findAllByEmployerIdInAndIsActive(employeeIds, true);

            Map<Long, EmployerContract> contractMap = contracts.stream()
                    .collect(Collectors.toMap(c -> c.getEmployer().getId(), Function.identity()));

            //create list for set all employers bonus amount with base salary
            List<EmployerContractResponse> employerContractResponse = new ArrayList<>();

            List<Long> employerIds = employerContractResponse.stream()
                    .map(EmployerContractResponse::getEmployerId)
                    .distinct()
                    .toList();

            Map<Long, Employer> employerMap = employerRepository.findAllById(employerIds)
                    .stream()
                    .collect(Collectors.toMap(Employer::getId, e -> e));

            List<GradeHistory> gradeHistories = new ArrayList<>();

            if (marketGradeHistory.getGrade().getGradeType() == GradeType.Fixed) {

                if (totalSale.compareTo(marketGradeHistory.getMinThreshold()) <= 0) {
                    throw new TargetNotReachedException("Market", "min threshold",
                            marketGradeHistory.getMinThreshold());
                }

                for (GradeRuleResponse gradeRuleResponse : gradeRules) {
                    EmployerContract contract = contractMap.get(gradeRuleResponse.getEmployeeId());

                    if (contract != null) {
                        EmployerContractResponse response = new EmployerContractResponse();

                        response.setGradeId(gradeRuleResponse.getGradeId());
                        response.setPositionId(contract.getPosition().getId());
                        response.setMarketId(contract.getMarket().getId());
                        response.setEmployerId(contract.getEmployer().getId());
                        response.setBaseSalary(contract.getBaseSalary());
                        response.setBonusAmount(gradeRuleResponse.getBonusAmount());
                        response.setCurrency(contract.getCurrency());
                        response.setValidFrom(contract.getValidFrom());
                        response.setValidTo(contract.getValidTo());

                        BigDecimal baseSalary = Optional.ofNullable(contract.getBaseSalary()).orElse(BigDecimal.ZERO);
                        BigDecimal bonusAmount = Optional.ofNullable(gradeRuleResponse.getBonusAmount()).orElse(BigDecimal.ZERO);
                        response.setTotalAmount(baseSalary.add(bonusAmount));

                        employerContractResponse.add(response);
                    }
                }

                log.info("employerContract response : {}", employerContractResponse);

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

            if (marketGradeHistory.getGrade().getGradeType() == GradeType.Percent) {
                if (totalSale.compareTo(marketGradeHistory.getMinThreshold()) <= 0) {
                    throw new TargetNotReachedException("Market", "threshold", marketGradeHistory.getMinThreshold());
                }
                int countOfSpecialEmployer = gradeRules.size();

                int totalCountOfEmployer = employerContractRepository.countByMarketIdAndIsActive(marketId, true);

                int countOfNotSpecialEmployer = totalCountOfEmployer - countOfSpecialEmployer;

                BigDecimal fixAmount = totalSale.multiply(
                        marketGradeHistory.getGrade().getGeneralPercent().divide(BigDecimal.valueOf(100),
                                4, RoundingMode.HALF_UP));

                BigDecimal sumOfPercent = BigDecimal.ZERO;

                List<GradeRuleResponse> updatedRules = new ArrayList<>();

                for (GradeRuleResponse rule : gradeRules) {
                    BigDecimal bonusPercent = rule.getBonusPercent();
                    sumOfPercent = sumOfPercent.add(bonusPercent);
                    Long employeeId = rule.getEmployeeId();
                    // formula: (fixAmount * bonusPercent) / 100
                    BigDecimal newPriceOfGradeRule = fixAmount.multiply(bonusPercent)
                            .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

                    updatedRules.add(new GradeRuleResponse(
                            rule.getGradeId(),
                            rule.getPositionId(),
                            employeeId,
                            rule.getMarketId(),
                            rule.getBonusPercent(),
                            newPriceOfGradeRule,
                            rule.getCurrency()
                    ));
                }

                BigDecimal newPercent = BigDecimal.valueOf(100).subtract(sumOfPercent);

                BigDecimal newPriceOfNotSpecialEmployer = fixAmount.multiply(
                        newPercent.divide(BigDecimal.valueOf(100),
                                4, RoundingMode.HALF_UP));

                BigDecimal amountPerEmployer = BigDecimal.ZERO;

                if (countOfNotSpecialEmployer > 0) {
                    amountPerEmployer = newPriceOfNotSpecialEmployer.divide(
                            BigDecimal.valueOf(countOfNotSpecialEmployer),
                            2,
                            RoundingMode.HALF_UP
                    );
                }
//                log.info("amountPerEmployer : {}", amountPerEmployer);

                for (GradeRuleResponse gradeRuleResponse : updatedRules) {
                    EmployerContract contract = contractMap.get(gradeRuleResponse.getEmployeeId());
                    if (contract != null) {
                        EmployerContractResponse response = new EmployerContractResponse();

                        response.setGradeId(gradeRuleResponse.getGradeId());
                        response.setPositionId(contract.getPosition().getId());
                        response.setMarketId(contract.getMarket().getId());
                        response.setEmployerId(contract.getEmployer().getId());
                        response.setBaseSalary(contract.getBaseSalary());
                        response.setBonusAmount(gradeRuleResponse.getBonusAmount());
                        response.setCurrency(contract.getCurrency());
                        response.setValidFrom(contract.getValidFrom());
                        response.setValidTo(contract.getValidTo());

                        BigDecimal baseSalary = Optional.ofNullable(contract.getBaseSalary()).orElse(BigDecimal.ZERO);
                        BigDecimal bonusAmount = Optional.ofNullable(gradeRuleResponse.getBonusAmount()).orElse(BigDecimal.ZERO);
                        response.setTotalAmount(baseSalary.add(bonusAmount));

                        employerContractResponse.add(response);
                    }
                }

                List<EmployerContractResponse> employerContracts = employerContractRepository
                        .findByEmployerIdNotIn(employeeIds, marketId, true);

                log.info("employerContracts (grade ruleda olmayan istifadeciler gelmelidir) : {}", employerContracts);


                List<EmployerContractResponse> notSpecificEmployer = new ArrayList<>();

                for (EmployerContractResponse employerContractResponse1 : employerContracts) {
                    EmployerContractResponse response = new EmployerContractResponse();

                    response.setGradeId(employerContractResponse1.getGradeId());
                    response.setPositionId(employerContractResponse1.getPositionId());
                    response.setMarketId(employerContractResponse1.getMarketId());
                    response.setEmployerId(employerContractResponse1.getEmployerId());
                    response.setBaseSalary(employerContractResponse1.getBaseSalary());

                    log.info("amountPerEmployer before set: {}", amountPerEmployer);

                    response.setBonusAmount(amountPerEmployer);

                    log.info("bonusAmount after set: {}", response.getBonusAmount());

                    response.setCurrency(employerContractResponse1.getCurrency());
                    response.setValidFrom(employerContractResponse1.getValidFrom());
                    response.setValidTo(employerContractResponse1.getValidTo());

                    response.setTotalAmount(amountPerEmployer.add(employerContractResponse1.getBaseSalary()));

                    notSpecificEmployer.add(response);
                }
                log.info("SON HAL -> employerContracts (grade ruleda olmayan istifadeciler gelmelidir) : {}",
                        notSpecificEmployer);

                log.info("employerContractResponse (adi isci olmayanlar hansiki grade ruleda qeyd edilenler) : {}",
                        employerContractResponse);

                employerContractResponse.addAll(notSpecificEmployer);

                log.info("butun list : {}", employerContractResponse);

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
}