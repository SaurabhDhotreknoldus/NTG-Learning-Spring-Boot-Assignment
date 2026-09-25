package com.nashtech.employeeservice.service;

import com.nashtech.employeeservice.dto.DepartmentSalaryStats;
import com.nashtech.employeeservice.dto.EmployeeCreateRequest;
import com.nashtech.employeeservice.dto.EmployeeResponse;
import com.nashtech.employeeservice.dto.EmployeeUpdateRequest;
import com.nashtech.employeeservice.dto.PagedResponse;
import com.nashtech.employeeservice.entity.Department;
import com.nashtech.employeeservice.entity.EmployeeStatus;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service contract defining core employee lifecycle and business operations.
 */
public interface EmployeeService {

    /**
     * Create and onboard a new employee.
     */
    EmployeeResponse createEmployee(EmployeeCreateRequest request);

    /**
     * Fetch all employees with pagination and sorting.
     */
    PagedResponse<EmployeeResponse> getAllEmployees(Pageable pageable);

    /**
     * Retrieve single employee details by primary identifier.
     */
    EmployeeResponse getEmployeeById(Long id);

    /**
     * Update existing employee details.
     */
    EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request);

    /**
     * Delete employee by identifier.
     */
    void deleteEmployee(Long id);

    /**
     * Filter employees by department.
     */
    PagedResponse<EmployeeResponse> getEmployeesByDepartment(Department department, Pageable pageable);

    /**
     * Search employees with dynamic multi-criteria filter.
     */
    PagedResponse<EmployeeResponse> searchEmployees(
            Department department,
            EmployeeStatus status,
            BigDecimal minSalary,
            BigDecimal maxSalary,
            Pageable pageable
    );

    /**
     * Retrieve aggregated department salary metrics.
     */
    List<DepartmentSalaryStats> getDepartmentSalaryStats();
}
