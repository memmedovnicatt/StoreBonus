package com.nicat.storebonus.mapper;

import com.nicat.storebonus.dtos.request.GradeRequest;
import com.nicat.storebonus.entities.Grade;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GradeMapper {

    void updateEntityForFields(GradeRequest gradeRequest, @MappingTarget Grade grade);
}
