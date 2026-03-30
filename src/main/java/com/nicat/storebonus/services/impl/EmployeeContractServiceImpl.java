package com.nicat.storebonus.services.impl;

import com.nicat.storebonus.dtos.request.EmployeeContractRequest;
import com.nicat.storebonus.entities.Employee;
import com.nicat.storebonus.entities.EmployeeContract;
import com.nicat.storebonus.entities.Market;
import com.nicat.storebonus.exceptions.handler.ResourceNotFoundException;
import com.nicat.storebonus.mapper.EmployeeContractMapper;
import com.nicat.storebonus.repositories.EmployeeContractRepository;
import com.nicat.storebonus.services.EmployeeContractService;
import com.nicat.storebonus.services.EmployeeService;
import com.nicat.storebonus.services.MarketService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeContractServiceImpl implements EmployeeContractService {

    EmployeeContractRepository employeeContractRepository;
    EmployeeService employeeService;
    MarketService marketService;
    EmployeeContractMapper employeeContractMapper;

    @Override
    public void createContract(EmployeeContractRequest request) {
        log.info("[Contract Creation] Started for Employee ID: {} at Market ID: {}",
                request.employerId(), request.marketId());

        Market market = marketService
                .checkExistsMarket(request.marketId());
        log.debug("Market validated: {} (ID: {})", market.getName(), market.getId());

        Employee employee = employeeService
                .checkExistsEmployer(request.employerId());
        log.debug("Employee validated: {} {} (ID: {})",
                employee.getName(), employee.getSurname(), employee.getId());

        EmployeeContract savedEmployeeContract = employeeContractMapper
                .toEmployeeContract(request);
        log.debug("EmployeeContractRequest mapped to EmployeeContract");

        savedEmployeeContract.setEmployee(employee);
        savedEmployeeContract.setPosition(employee.getPosition());
        savedEmployeeContract.setMarket(market);

        employeeContractRepository.save(savedEmployeeContract);
        log.debug("EmployeeContract saved with successfully");
    }

    @Override
    public void deactivateEmployee(Long employeeId) {
        log.info("Request started for Employee ID: {}", employeeId);
        EmployeeContract employeeContract = employeeContractRepository
                .findByEmployeeIdAndIsActive(employeeId, true)
                .orElseThrow(() -> {
                    log.warn("[Employee Deactivation Failed] No active contract found for Employee ID: {}", employeeId);
                    return new ResourceNotFoundException("Employee", "id", employeeId);
                });

        log.debug("[Employee Deactivation] Found active contract (ID: {}) for Employee: {}",
                employeeContract.getId(), employeeContract.getEmployee().getName());

        employeeContract.setActive(false);
        employeeContract.setLeavingDate(LocalDate.now());
        employeeContractRepository.save(employeeContract);
        log.debug("Status set to INACTIVE and Leaving Date set to {}", LocalDate.now());
    }
}