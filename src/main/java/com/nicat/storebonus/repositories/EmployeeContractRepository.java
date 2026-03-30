package com.nicat.storebonus.repositories;

import com.nicat.storebonus.dtos.response.EmployeeContractResponse;
import com.nicat.storebonus.entities.EmployeeContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeContractRepository extends JpaRepository<EmployeeContract, Long> {
    List<EmployeeContract> findAllByEmployeeIdInAndIsActive(List<Long> employeeIds, boolean isActive);

    int countByMarketIdAndIsActive(Long marketId, boolean isActive);

    @Query("""
            SELECT new com.nicat.storebonus.dtos.response.EmployeeContractResponse(
                null,
                e.position.id,
                e.market.id,
                e.employee.id,
                e.baseSalary,
                null,
                null,
                e.currency,
                e.validFrom,
                e.validTo
            )
            FROM EmployeeContract e
            WHERE e.employee.id NOT IN :employerIds
            AND e.market.id=:marketId
            AND e.isActive=true
            """)
    List<EmployeeContractResponse> findByEmployeeIdNotIn(@Param("employeeIds") List<Long> employeeIds,
                                                         Long marketId,
                                                         boolean isActive);


    @Query("""
            SELECT new com.nicat.storebonus.dtos.response.EmployeeContractResponse(
                null,
                e.position.id,
                e.market.id,
                e.employee.id,
                e.baseSalary,
                null,
                null,
                e.currency,
                e.validFrom,
                e.validTo
            )
            FROM EmployeeContract e
            WHERE e.market.id=:marketId
            AND e.isActive=true
            """)
    List<EmployeeContractResponse> findAllByMarketIdAndIsActive(Long marketId, boolean isActive);

    Optional<EmployeeContract> findByEmployeeIdAndIsActive(Long employeeId, boolean isActive);

    List<EmployeeContract> findByLeavingDateIsNotNull();
}