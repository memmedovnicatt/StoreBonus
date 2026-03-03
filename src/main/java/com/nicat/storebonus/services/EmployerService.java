package com.nicat.storebonus.services;

import com.nicat.storebonus.dtos.request.EmployerRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface EmployerService {
    void create(@Valid EmployerRequest employerRequest);
}
