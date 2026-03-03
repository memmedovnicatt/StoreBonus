package com.nicat.storebonus.services;

import com.nicat.storebonus.dtos.request.EmployerRequest;
import com.nicat.storebonus.entities.Employer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public interface EmployerService {
    void create(@Valid EmployerRequest employerRequest);

    Employer checkExistsEmployer(@NotNull Long employerId);
}
