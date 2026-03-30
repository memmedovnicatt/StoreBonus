package com.nicat.storebonus.services.impl;

import com.nicat.storebonus.dtos.request.SaleRequest;
import com.nicat.storebonus.dtos.response.FinalSalaryResponse;
import com.nicat.storebonus.dtos.response.MarketSalesResponse;
import com.nicat.storebonus.entities.Employee;
import com.nicat.storebonus.entities.EmployeeContract;
import com.nicat.storebonus.entities.Market;
import com.nicat.storebonus.entities.Sale;
import com.nicat.storebonus.exceptions.handler.ResourceNotFoundException;
import com.nicat.storebonus.mapper.SaleMapper;
import com.nicat.storebonus.repositories.EmployeeContractRepository;
import com.nicat.storebonus.repositories.MarketRepository;
import com.nicat.storebonus.repositories.SaleRepository;
import com.nicat.storebonus.services.EmployeeService;
import com.nicat.storebonus.services.MarketService;
import com.nicat.storebonus.services.SaleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SaleServiceImpl implements SaleService {

    SaleRepository saleRepository;
    SaleMapper saleMapper;
    EmployeeService employeeService;
    MarketService marketService;
    EmployeeContractRepository employeeContractRepository;
    MarketRepository marketRepository;

    @Override
    public void create(SaleRequest saleRequest) {
        Market market = marketService.checkExistsMarket(saleRequest.marketId());

        Employee employee = employeeService.checkExistsEmployer(saleRequest.employeeId());

        Sale savedSale = saleMapper.toSale(saleRequest);
        savedSale.setEmployee(employee);
        savedSale.setMarket(market);

        saleRepository.save(savedSale);
    }

    @Override
    public List<FinalSalaryResponse> calculateFinalSalary() {
        log.debug("calculateFinalSalary method was started");
        List<EmployeeContract> list = employeeContractRepository.findByLeavingDateIsNotNull();
        log.debug("list size is : {}", list.size());
        List<FinalSalaryResponse> result = new ArrayList<>();

        for (EmployeeContract employeeContract : list) {

            long workDays = ChronoUnit.DAYS.between(
                    employeeContract.getValidFrom(),
                    employeeContract.getLeavingDate());
            log.debug("workDay : {}", workDays);

            BigDecimal dailySalary = employeeContract.getBaseSalary()
                    .divide(BigDecimal.valueOf(31), 2, RoundingMode.HALF_UP);
            log.debug("dailySalary : {}", dailySalary);

            BigDecimal finalSalary = dailySalary.multiply(BigDecimal.valueOf(workDays));
            log.debug("finalSalary : {}", finalSalary);

            FinalSalaryResponse dto = new FinalSalaryResponse();
            dto.setEmployeeId(employeeContract.getEmployee().getId());
            dto.setEmployeeName(employeeContract.getEmployee().getName());
            dto.setBaseSalary(employeeContract.getBaseSalary());
            dto.setWorkedDays(workDays);
            dto.setFinalSalary(finalSalary);

            result.add(dto);
        }
        log.debug("successfully mapped to FinalSalaryResponse dto");
        return result;
    }

    @Override
    public List<MarketSalesResponse> getSalesOfMarket(Long marketId) {
        Market market = marketRepository.findById(marketId).orElse(null);
        if (market == null) {
            throw new ResourceNotFoundException("Market", "id", marketId);
        }
        List<Sale> list = saleRepository.findByMarketId(marketId);

        return saleMapper.toListMarketSalesResponse(list);
    }
}
