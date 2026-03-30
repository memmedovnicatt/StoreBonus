package com.nicat.storebonus.services;

import com.nicat.storebonus.dtos.request.EmployeeRequest;
import com.nicat.storebonus.entities.Employee;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public interface EmployeeService {
    void create(@Valid EmployeeRequest employeeRequest);

    Employee checkExistsEmployer(@NotNull Long employerId);
}
