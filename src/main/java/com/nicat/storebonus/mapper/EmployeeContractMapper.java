package com.nicat.storebonus.mapper;


import com.nicat.storebonus.dtos.request.EmployeeContractRequest;
import com.nicat.storebonus.dtos.response.EmployeeContractResponse;
import com.nicat.storebonus.entities.EmployeeContract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeContractMapper {
    EmployeeContract toEmployeeContract(EmployeeContractRequest employeeContractRequest);



    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "market.id", target = "marketId")
    EmployeeContractResponse toEmployeeContractResponse(EmployeeContract employeeContract);
}
