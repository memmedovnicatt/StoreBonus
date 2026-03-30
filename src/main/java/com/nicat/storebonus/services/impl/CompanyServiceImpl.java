package com.nicat.storebonus.services.impl;

import com.nicat.storebonus.dtos.request.CompanyRequest;
import com.nicat.storebonus.dtos.response.ApiResponse;
import com.nicat.storebonus.dtos.response.ResponseMessage;
import com.nicat.storebonus.entities.Company;
import com.nicat.storebonus.exceptions.handler.ResourceNotFoundException;
import com.nicat.storebonus.repositories.CompanyRepository;
import com.nicat.storebonus.services.CompanyService;
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
public class CompanyServiceImpl implements CompanyService {
    CompanyRepository companyRepository;


    @Override
    public void create(CompanyRequest companyRequest) {
        log.info("Starting to create a new company with name: {} and location: {}",
                companyRequest.name(), companyRequest.location());

        Company company = Company.builder()
                .location(companyRequest.location())
                .name(companyRequest.name())
                .build();
        log.info("Company successfully created with ID: {}", company.getId());
        companyRepository.save(company);
        log.debug("Company successfully saved");
    }

    @Override
    public Company checkCompanyExists(Long companyId) {
        log.info("Checking existence of company with ID: {}", companyId);
        Company company = companyRepository.findById(companyId).orElse(null);
        if (company == null) {
            log.warn("Company not found with ID: {}", companyId);
            throw new ResourceNotFoundException("Company", "id", companyId);
        }
        log.info("Company found: {} (ID: {})", company.getName(), companyId);
        return company;
    }

    @Override
    public void delete(Long id) {
        log.info("Request received to deactivate company with ID: {}", id);
        Company company = companyRepository.findById(id).orElse(null);
        if (company == null) {
            log.warn("Company with ID: {} is already inactive. Skipping update.", id);
            throw new ResourceNotFoundException("Company", "id", id);
        }
        company.setActive(false);
        companyRepository.save(company);
        log.info("Company '{}' (ID: {}) has been successfully deactivated.",
                company.getName(), id);
    }

    @Override
    public List<Company> getAll() {
        log.info("Request to retrieve all active companies started.");
        List<Company> activeCompanies = companyRepository.findAllByIsActiveTrue();
        if (activeCompanies.isEmpty()) {
            log.warn("No active companies found in the database.");
        } else {
            log.info("Successfully retrieved {} active companies.", activeCompanies.size());
        }
        return activeCompanies;
    }
}