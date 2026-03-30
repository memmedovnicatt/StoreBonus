package com.nicat.storebonus.services.impl;

import com.nicat.storebonus.dtos.request.EmployeeRequest;
import com.nicat.storebonus.entities.Employee;
import com.nicat.storebonus.entities.Position;
import com.nicat.storebonus.exceptions.handler.ResourceNotFoundException;
import com.nicat.storebonus.mapper.EmployeeMapper;
import com.nicat.storebonus.repositories.EmployeeRepository;
import com.nicat.storebonus.services.EmployeeService;
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
public class EmployeeServiceImpl implements EmployeeService {
    EmployeeRepository employeeRepository;
    PositionService positionService;
    EmployeeMapper employeeMapper;

    @Override
    public void create(EmployeeRequest employeeRequest) {
        log.info("Starting to create a new Employee");
        Position position = positionService
                .checkExistsPosition(employeeRequest.positionId());
        log.info("Checking existence of position with ID: {}", position.getId());

        Employee savedEmployee = employeeMapper.toEmployer(employeeRequest);
        savedEmployee.setPosition(position);
        employeeRepository.save(savedEmployee);
        log.info("Employee was saved successfully");
    }

    @Override
    public Employee checkExistsEmployer(Long employerId) {
        return employeeRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer", "id", employerId));
    }
}