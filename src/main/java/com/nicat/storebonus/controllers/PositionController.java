package com.nicat.storebonus.controllers;

import com.nicat.storebonus.dtos.request.PositionRequest;
import com.nicat.storebonus.dtos.response.ApiResponse;
import com.nicat.storebonus.dtos.response.PositionResponse;
import com.nicat.storebonus.dtos.response.ResponseMessage;
import com.nicat.storebonus.services.PositionService;
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
@RequestMapping("/positions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Position APIs")
public class PositionController {
    PositionService positionService;

    @Operation(summary = "Create a new position")
    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> createPosition(@Valid @RequestBody PositionRequest positionRequest) {
        positionService.create(positionRequest);
        return ResponseEntity.ok(
                ApiResponse.success(null, ResponseMessage.SUCCESS_CREATE)
        );
    }

    @Operation(summary = "Get all positions")
    @GetMapping()
    public ResponseEntity<ApiResponse<List<PositionResponse>>> getAll() {
        List<PositionResponse> responseList = positionService.findAll();
        return ResponseEntity.ok(
                ApiResponse.success(responseList, ResponseMessage.SUCCESS_FETCH)
        );
    }

    @Operation(summary = "Delete a position by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePosition(@PathVariable Long id) {
        positionService.delete(id);
        return ResponseEntity.ok
                (ApiResponse.success(null, ResponseMessage.SUCCESS_DELETE));
    }
}