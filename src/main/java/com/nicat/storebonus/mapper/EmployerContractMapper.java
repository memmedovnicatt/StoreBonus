package com.nicat.storebonus.mapper;


import com.nicat.storebonus.dtos.request.EmployerContractRequest;
import com.nicat.storebonus.dtos.response.EmployerContractResponse;
import com.nicat.storebonus.entities.EmployerContract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployerContractMapper {
    EmployerContract toEmployerContract(EmployerContractRequest employerContractRequest);



    @Mapping(source = "employer.id", target = "employerId")
    @Mapping(source = "market.id", target = "marketId")
    EmployerContractResponse toEmployerContractResponse(EmployerContract employerContract);
}
