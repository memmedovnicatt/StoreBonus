package com.nicat.storebonus.mapper;

import com.nicat.storebonus.dtos.response.MarketGradeHistoryResponse;
import com.nicat.storebonus.entities.GradeHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GradeHistoryMapper {

    @Mapping(source = "employee.name", target = "employeeName")
    @Mapping(source = "employee.surname", target = "employeeSurname")
    @Mapping(source = "employee.phoneNumber", target = "phoneNumber")
    @Mapping(source = "employee.position.name", target = "positionName")
    @Mapping(source = "market.name", target = "marketName")
    MarketGradeHistoryResponse toGradeHistory(GradeHistory gradeHistory);

    List<MarketGradeHistoryResponse> toListGradeHistory(List<GradeHistory> gradeHistories);
}
