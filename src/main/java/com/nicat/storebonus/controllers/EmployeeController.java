package com.nicat.storebonus.controllers;

import com.nicat.storebonus.dtos.request.EmployeeContractRequest;
import com.nicat.storebonus.dtos.request.EmployeeRequest;
import com.nicat.storebonus.dtos.response.ApiResponse;
import com.nicat.storebonus.dtos.response.ResponseMessage;
import com.nicat.storebonus.services.EmployeeContractService;
import com.nicat.storebonus.services.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Employee APIs")
public class EmployeeController {
    EmployeeService employeeService;
    EmployeeContractService employeeContractService;

    @Operation(summary = "Create an new Employee")
    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> createEmployee(@Valid @RequestBody EmployeeRequest employeeRequest) {
        employeeService.create(employeeRequest);
        return ResponseEntity.ok(ApiResponse.success(null, ResponseMessage.SUCCESS_CREATE));
    }

    @Operation(summary = "Create a new contract with Employee")
    @PostMapping("/contract")
    public ResponseEntity<ApiResponse<Void>> createEmployeeContract(@Valid @RequestBody EmployeeContractRequest employeeContractRequest) {
        employeeContractService.createContract(employeeContractRequest);
        return ResponseEntity.ok(ApiResponse.success(null, ResponseMessage.SUCCESS_CREATE));
    }

    @Operation(summary = "Deactivate employee by EmployeeID ")
    @PutMapping("/deactivate/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateEmployee(@PathVariable("id") Long employeeId) {
        employeeContractService.deactivateEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.success(null, ResponseMessage.SUCCESS_FETCH)); //change response message
    }
}