package com.nicat.storebonus.controllers;


import com.nicat.storebonus.dtos.request.CompanyRequest;
import com.nicat.storebonus.dtos.response.ApiResponse;
import com.nicat.storebonus.dtos.response.ResponseMessage;
import com.nicat.storebonus.entities.Company;
import com.nicat.storebonus.services.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Company APIs")
public class CompanyController {

    CompanyService companyService;

    @Operation(summary = "Create a new company")
    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> createCompany(@Valid @RequestBody CompanyRequest companyRequest) {
        companyService.create(companyRequest);
        return ResponseEntity.ok(
                ApiResponse.success(null, ResponseMessage.SUCCESS_CREATE)
        );
    }

    @Operation(summary = "Delete a company by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(@PathVariable Long id) {
        companyService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, ResponseMessage.SUCCESS_DELETE));
    }

    @Operation(summary = "Get all companies")
    @GetMapping()
    public ResponseEntity<ApiResponse<List<Company>>> getAll() {
        List<Company> list = companyService.getAll();
        return ResponseEntity.ok(
                ApiResponse.success(list, ResponseMessage.SUCCESS_FETCH)
        );
    }
}