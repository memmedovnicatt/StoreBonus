package com.nicat.storebonus.services.impl;

import com.nicat.storebonus.dtos.request.WareHouseRequest;
import com.nicat.storebonus.entities.Company;
import com.nicat.storebonus.entities.WareHouse;
import com.nicat.storebonus.exceptions.handler.ResourceNotFoundException;
import com.nicat.storebonus.repositories.WareHouseRepository;
import com.nicat.storebonus.services.CompanyService;
import com.nicat.storebonus.services.WareHouseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.generic.RET;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WareHouseServiceImpl implements WareHouseService {
    WareHouseRepository wareHouseRepository;
    CompanyService companyService;

    @Override
    public void create(WareHouseRequest wareHouseRequest) {
        log.info("Create was started for WareHouse");
        Company company = companyService.checkCompanyExists(wareHouseRequest.companyId());
        log.debug("Company validated:{}", company.getId());

        WareHouse wareHouse = WareHouse.builder()
                .location(wareHouseRequest.location())
                .name(wareHouseRequest.name())
                .company(company)
                .build();

        wareHouseRepository.save(wareHouse);
        log.info("WareHouse created and saved successfully");
    }

    @Override
    public WareHouse checkExistsWareHouse(Long wareHouseId) {
        WareHouse wareHouse = wareHouseRepository.findById(wareHouseId)
                .orElse(null);
        if (wareHouse == null) {
            log.info("WareHouse was not found with ID:{}", wareHouseId);
            throw new ResourceNotFoundException("WareHouse", "id", wareHouseId);
        }
        return wareHouse;
    }

    @Override
    public List<WareHouse> getAll() {
        return wareHouseRepository.findAll();
    }
}