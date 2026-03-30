package com.nicat.storebonus.mapper;

import com.nicat.storebonus.dtos.request.SaleRequest;
import com.nicat.storebonus.dtos.response.MarketSalesResponse;
import com.nicat.storebonus.entities.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SaleMapper {
    Sale toSale(SaleRequest saleRequest);

    List<MarketSalesResponse> toListMarketSalesResponse(List<Sale> list);

    @Mapping(source = "employee.name", target = "employeeName")
    @Mapping(source = "employee.name", target = "employeeSurname")
    @Mapping(source = "market.name", target = "marketName")
    MarketSalesResponse toListMarketSalesResponse(Sale sale);
}
