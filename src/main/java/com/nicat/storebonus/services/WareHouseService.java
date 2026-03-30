package com.nicat.storebonus.services;

import com.nicat.storebonus.dtos.request.WareHouseRequest;
import com.nicat.storebonus.entities.WareHouse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public interface WareHouseService {
    void create(@Valid WareHouseRequest wareHouseRequest);

    WareHouse checkExistsWareHouse(Long wareHouseId);

    List<WareHouse> getAll();
}
