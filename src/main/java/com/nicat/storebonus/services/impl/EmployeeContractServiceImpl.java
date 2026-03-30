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
    public void createContract(EmployeeContractRequest employeeContractRequest) {
        log.debug("createContract method was started for employee contract service");

        Market market = marketService
                .checkExistsMarket(employeeContractRequest.marketId());
        log.debug("marketId: {}", market.getId());

        Employee employee = employeeService
                .checkExistsEmployer(employeeContractRequest.employerId());
        log.debug("employeeId: {}", employee.getId());

        EmployeeContract savedEmployeeContract = employeeContractMapper
                .toEmployeeContract(employeeContractRequest);

        savedEmployeeContract.setEmployee(employee);
        savedEmployeeContract.setPosition(employee.getPosition());
        savedEmployeeContract.setMarket(market);

        employeeContractRepository.save(savedEmployeeContract);
        log.debug("save successfully for employee contract");
    }

    @Override
    public void deactivateEmployee(Long employeeId) {
        log.debug("deactivateEmployee method was started");
        EmployeeContract employeeContract = employeeContractRepository.findByEmployeeIdAndIsActive(employeeId, true)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
        log.debug("employeeContract : {}", employeeContract.getEmployee().getId());
        employeeContract.setActive(false);
        employeeContract.setLeavingDate(LocalDate.now());
        log.debug("employee status changed");
        employeeContractRepository.save(employeeContract);
        log.debug("employeeContract was saved");
    }
}
