package com.nicat.storebonus.services;

import com.nicat.storebonus.dtos.request.GradeRequest;
import com.nicat.storebonus.entities.Grade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

@Service
public interface GradeService {
    void create(@Valid GradeRequest gradeRequest);

    Grade checkExistsGrade(Long gradeId);
}
