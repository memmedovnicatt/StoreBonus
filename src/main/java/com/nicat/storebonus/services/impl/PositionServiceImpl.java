package com.nicat.storebonus.services.impl;

import com.nicat.storebonus.dtos.request.PositionRequest;
import com.nicat.storebonus.dtos.response.ApiResponse;
import com.nicat.storebonus.dtos.response.PositionResponse;
import com.nicat.storebonus.dtos.response.ResponseMessage;
import com.nicat.storebonus.entities.Position;
import com.nicat.storebonus.exceptions.handler.ResourceAlreadyExistsException;
import com.nicat.storebonus.exceptions.handler.ResourceNotFoundException;
import com.nicat.storebonus.mapper.PositionMapper;
import com.nicat.storebonus.repositories.PositionRepository;
import com.nicat.storebonus.services.PositionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PositionServiceImpl implements PositionService {

    PositionRepository positionRepository;
    PositionMapper positionMapper;

    @Override
    public void create(PositionRequest positionRequest) {
        log.info("Create was started for Position with name: {}", positionRequest.name());
        boolean checkPositionName = positionRepository.existsByName(positionRequest.name());
        if (checkPositionName) {
            log.warn("Position name:{} was already exists", positionRequest.name());
            throw new ResourceAlreadyExistsException(positionRequest.name());
        }

        Position position = Position.builder()
                .name(positionRequest.name())
                .build();

        positionRepository.save(position);
        log.info("Position created and saved");
    }

    @Override
    public List<PositionResponse> findAll() {
        log.info("Request to retrieve all active positions started.");
        List<Position> positions = positionRepository.findAllByIsActiveTrue();
        return positionMapper.toListPositionResponse(positions);
    }

    @Override
    public Position checkExistsPosition(Long positionId) {
        return positionRepository.findById(positionId)
                .orElseThrow(() -> new ResourceNotFoundException("Position", "id", positionId));
    }

    @Override
    public void delete(Long id) {
        Position position = positionRepository.findById(id).orElse(null);
        if (position == null) {
            throw new ResourceNotFoundException("Position", "id", id);
        }
        position.setActive(false);
        position.setDeletedAt(LocalDateTime.now());
        positionRepository.save(position);
    }
}