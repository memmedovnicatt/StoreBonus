package com.nicat.storebonus.controllers;


import com.nicat.storebonus.dtos.request.CompanyRequest;
import com.nicat.storebonus.dtos.request.WareHouseRequest;
import com.nicat.storebonus.dtos.response.ApiResponse;
import com.nicat.storebonus.dtos.response.ResponseMessage;
import com.nicat.storebonus.services.WareHouseService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WareHouseController {
    WareHouseService wareHouseService;


    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> createWareHouse(@Valid @RequestBody WareHouseRequest wareHouseRequest) {
        wareHouseService.create(wareHouseRequest);
        return ResponseEntity.ok(
                ApiResponse.success(null, ResponseMessage.SUCCESS_CREATE)
        );
    }
}
