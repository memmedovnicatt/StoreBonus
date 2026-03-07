package com.nicat.storebonus.services;

import com.nicat.storebonus.dtos.request.GradeRuleRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface GradeRuleService {
    void create(@Valid GradeRuleRequest gradeRuleRequest);
}
