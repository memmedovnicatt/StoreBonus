package com.nicat.storebonus.controllers;

import com.nicat.storebonus.dtos.request.WareHouseRequest;
import com.nicat.storebonus.dtos.response.ApiResponse;
import com.nicat.storebonus.dtos.response.ResponseMessage;
import com.nicat.storebonus.entities.WareHouse;
import com.nicat.storebonus.services.WareHouseService;
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
@RequestMapping("/warehouses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "WareHouse APIs")
public class WareHouseController {
    WareHouseService wareHouseService;

    @Operation(summary = "Create a new ware house")
    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> createWareHouse(@Valid @RequestBody WareHouseRequest wareHouseRequest) {
        wareHouseService.create(wareHouseRequest);
        return ResponseEntity.ok(
                ApiResponse.success(null, ResponseMessage.SUCCESS_CREATE)
        );
    }

    @Operation(summary = "Get all ware houses")
    @GetMapping()
    public ResponseEntity<ApiResponse<List<WareHouse>>> getAll() {
        List<WareHouse> list = wareHouseService.getAll();
        return ResponseEntity.ok(
                ApiResponse.success(list, ResponseMessage.SUCCESS_FETCH)
        );
    }
}
