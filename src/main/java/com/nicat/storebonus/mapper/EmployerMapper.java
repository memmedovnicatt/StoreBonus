package com.nicat.storebonus.mapper;


import com.nicat.storebonus.dtos.request.EmployerRequest;
import com.nicat.storebonus.entities.Employer;
import com.nicat.storebonus.entities.Position;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployerMapper {

    Employer toEmployer(EmployerRequest employerRequest);
}