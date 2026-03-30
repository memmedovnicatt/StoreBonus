package com.nicat.storebonus.mapper;


import com.nicat.storebonus.dtos.request.EmployeeRequest;
import com.nicat.storebonus.entities.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeMapper {

    Employee toEmployer(EmployeeRequest employeeRequest);
}