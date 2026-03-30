package com.nicat.storebonus.controllers;

import com.nicat.storebonus.dtos.request.GradeCalculationRequest;
import com.nicat.storebonus.dtos.request.GradeRequest;
import com.nicat.storebonus.dtos.request.GradeRuleRequest;
import com.nicat.storebonus.dtos.response.ApiResponse;
import com.nicat.storebonus.dtos.response.MarketGradeHistoryResponse;
import com.nicat.storebonus.dtos.response.ResponseMessage;
import com.nicat.storebonus.services.GradeRuleService;
import com.nicat.storebonus.services.GradeService;
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
@RequestMapping("/grades")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Grade APIs")
public class GradeController {

    GradeService gradeService;
    GradeRuleService gradeRuleService;

    @Operation(summary = "Create a new Grade")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createGrade(@Valid @RequestBody GradeRequest gradeRequest) {
        gradeService.create(gradeRequest);
        return ResponseEntity.ok(ApiResponse.success(null, ResponseMessage.SUCCESS_CREATE));
    }

    @Operation(
            summary = "Calculate and assign bonuses based on market grade types",
            description = "Executes a multi-step bonus calculation:\n\n" +
                    "* **Step 1:** Identifies the grade type for the given Market ID.\n" +
                    "* **Step 2:** Validates business rules against the selected grade type.\n" +
                    "* **Step 3:** Calculates and assigns bonus amounts to eligible employees."
    )
    @PostMapping("/bonus-calculation")
    public ResponseEntity<ApiResponse<Void>> calculateGrade(@Valid @RequestBody GradeCalculationRequest gradeCalculationRequest) {
        gradeService.calculateGrade(gradeCalculationRequest);
        return ResponseEntity.ok(ApiResponse.success(null, ResponseMessage.SUCCESS_CALCULATED));
    }

    @Operation(summary = "Create a new GradeRule for Market")
    @PostMapping("/rules")
    public ResponseEntity<ApiResponse<Void>> createGradePositionBonus(@Valid @RequestBody GradeRuleRequest gradeRuleRequest) {
        gradeRuleService.create(gradeRuleRequest);
        return ResponseEntity.ok(ApiResponse.success(null, ResponseMessage.SUCCESS_CREATE));
    }

    @Operation(summary = "Get all Grades")
    @GetMapping("/histories")
    public ResponseEntity<ApiResponse<List<MarketGradeHistoryResponse>>> getAll() {
        List<MarketGradeHistoryResponse> marketGradeHistoryResponse = gradeService.getAll();
        return ResponseEntity.ok(ApiResponse.success(marketGradeHistoryResponse, ResponseMessage.SUCCESS_FETCH));
    }

    @Operation(summary = "Partial update an existing grade by ID")
    @PutMapping("/id")
    public ResponseEntity<ApiResponse<Void>> update(@RequestParam Long id,
                                                    @RequestBody GradeRequest gradeRequest) {
        gradeService.update(id, gradeRequest);
        return ResponseEntity.ok(ApiResponse.success(null, ResponseMessage.SUCCESS_UPDATE));
    }
}