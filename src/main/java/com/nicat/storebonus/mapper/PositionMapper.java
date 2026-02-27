package com.nicat.storebonus.mapper;

import com.nicat.storebonus.dtos.response.PositionResponse;
import com.nicat.storebonus.entities.Position;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PositionMapper {
    List<PositionResponse> toListPositionResponse(List<Position> positions);
}
