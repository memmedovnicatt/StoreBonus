package com.nicat.storebonus.mapper;

import com.nicat.storebonus.dtos.request.SaleRequest;
import com.nicat.storebonus.entities.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SaleMapper {
    Sale toSale(SaleRequest saleRequest);
}
