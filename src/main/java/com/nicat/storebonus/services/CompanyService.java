package com.nicat.storebonus.services;

import com.nicat.storebonus.dtos.request.CompanyRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface CompanyService {
    void create(@Valid CompanyRequest companyRequest);
}