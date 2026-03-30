package com.nicat.storebonus.controllers;


import com.nicat.storebonus.dtos.request.MarketRequest;
import com.nicat.storebonus.dtos.response.ApiResponse;
import com.nicat.storebonus.dtos.response.ResponseMessage;
import com.nicat.storebonus.entities.Market;
import com.nicat.storebonus.services.MarketService;
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
@RequestMapping("/markets")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Market APIs")
public class MarketController {
    MarketService marketService;

    @Operation(summary = "Create a new market")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createMarket(@Valid @RequestBody MarketRequest marketRequest) {
        marketService.create(marketRequest);
        return ResponseEntity.ok(ApiResponse.success(null, ResponseMessage.SUCCESS_CREATE));
    }

    @Operation(summary = "Get all markets")
    @GetMapping()
    public ResponseEntity<ApiResponse<List<Market>>> getAll() {
        List<Market> list = marketService.getAll();
        return ResponseEntity.ok(ApiResponse.success(list, ResponseMessage.SUCCESS_FETCH));
    }

    @Operation(summary = "Delete a market by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMarket(@PathVariable Long id) {
        marketService.delete(id);
        return ResponseEntity.ok
                (ApiResponse.success(null, ResponseMessage.SUCCESS_DELETE));
    }
}
