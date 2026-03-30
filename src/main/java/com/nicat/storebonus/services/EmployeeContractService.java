package com.nicat.storebonus.services;

import com.nicat.storebonus.dtos.request.EmployeeContractRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface EmployeeContractService {
    void createContract(@Valid EmployeeContractRequest employeeContractRequest);
    void deactivateEmployee(Long employeeId);
}
