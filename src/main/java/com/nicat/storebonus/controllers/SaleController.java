package com.nicat.storebonus.controllers;


import com.nicat.storebonus.dtos.request.SaleRequest;
import com.nicat.storebonus.dtos.response.ApiResponse;
import com.nicat.storebonus.dtos.response.FinalSalaryResponse;
import com.nicat.storebonus.dtos.response.MarketSalesResponse;
import com.nicat.storebonus.dtos.response.ResponseMessage;
import com.nicat.storebonus.services.SaleService;
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
@RequestMapping("/sales")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Sale APIs")
public class SaleController {
    SaleService saleService;

    @Operation(summary = "Create a new Sale")
    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> createSale(@Valid @RequestBody SaleRequest saleRequest) {
        saleService.create(saleRequest);
        return ResponseEntity.ok(ApiResponse.success(null, ResponseMessage.SUCCESS_CREATE));
    }

    @Operation(
            summary = "Calculate total salary for employees",
            description = "Calculates the total salary based on actual working days.\n\n" +
                    "**Important Calculation Rules:**\n" +
                    "* **Standard Case:** Salary is based on recorded working days.\n" +
                    "* **Resigned Employees:** The calculation is strictly performed over a **31-day** month cycle."
    )
    @PostMapping("/final-salary")
    public ResponseEntity<ApiResponse<List<FinalSalaryResponse>>> calculateFinalSalary() {
        List<FinalSalaryResponse> list = saleService.calculateFinalSalary();
        return ResponseEntity.ok(ApiResponse.success(list, ResponseMessage.SUCCESS_CALCULATED));
    }

    @Operation(summary = "Get sales of Market")
    @GetMapping("/{marketId}")
    public ResponseEntity<ApiResponse<List<MarketSalesResponse>>> getSalesOfMarket(@PathVariable Long marketId) {
        List<MarketSalesResponse> list = saleService.getSalesOfMarket(marketId);
        return ResponseEntity.ok
                (ApiResponse.success(list, ResponseMessage.SUCCESS_FETCH));
    }
}
