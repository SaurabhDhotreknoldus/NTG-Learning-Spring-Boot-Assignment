package com.nashtech.employeeservice.service.impl;

import com.nashtech.employeeservice.dto.DepartmentSalaryStats;
import com.nashtech.employeeservice.dto.EmployeeCreateRequest;
import com.nashtech.employeeservice.dto.EmployeeResponse;
import com.nashtech.employeeservice.dto.EmployeeUpdateRequest;
import com.nashtech.employeeservice.dto.PagedResponse;
import com.nashtech.employeeservice.entity.Department;
import com.nashtech.employeeservice.entity.Employee;
import com.nashtech.employeeservice.entity.EmployeeStatus;
import com.nashtech.employeeservice.exception.DuplicateResourceException;
import com.nashtech.employeeservice.exception.ResourceNotFoundException;
import com.nashtech.employeeservice.mapper.EmployeeMapper;
import com.nashtech.employeeservice.repository.EmployeeRepository;
import com.nashtech.employeeservice.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Production-ready implementation of {@link EmployeeService}.
 * Enforces business constraints, manages transaction boundaries, and orchestrates repository calls.
 */
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    // Constructor injection - best practice for dependency injection and testability
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        log.info("Initiating employee creation for email: {}", request.email());

        // Domain validation: Ensure email uniqueness across organization
        if (employeeRepository.existsByEmail(request.email().trim().toLowerCase())) {
            throw new DuplicateResourceException("Employee", "email", request.email());
        }

        Employee employee = employeeMapper.toEntity(request);
        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee successfully persisted with ID: {}", savedEmployee.getId());

        return employeeMapper.toResponse(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<EmployeeResponse> getAllEmployees(Pageable pageable) {
        log.info("Fetching paginated employees with page: {}, size: {}, sort: {}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<Employee> page = employeeRepository.findAll(pageable);
        return PagedResponse.of(page.map(employeeMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        log.info("Fetching employee with ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        return employeeMapper.toResponse(employee);
    }

    @Override
    public EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request) {
        log.info("Updating employee with ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        employeeMapper.updateEntityFromDto(request, employee);
        Employee updatedEmployee = employeeRepository.save(employee);
        log.info("Employee with ID: {} successfully updated", updatedEmployee.getId());

        return employeeMapper.toResponse(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {
        log.info("Attempting to delete employee with ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        employeeRepository.delete(employee);
        log.info("Employee with ID: {} successfully deleted", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<EmployeeResponse> getEmployeesByDepartment(Department department, Pageable pageable) {
        log.info("Fetching employees filtered by department: {}", department);

        Page<Employee> page = employeeRepository.findByDepartment(department, pageable);
        return PagedResponse.of(page.map(employeeMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<EmployeeResponse> searchEmployees(
            Department department,
            EmployeeStatus status,
            BigDecimal minSalary,
            BigDecimal maxSalary,
            Pageable pageable) {
        log.info("Searching employees with department: {}, status: {}, minSalary: {}, maxSalary: {}",
                department, status, minSalary, maxSalary);

        Page<Employee> page = employeeRepository.searchEmployees(department, status, minSalary, maxSalary, pageable);
        return PagedResponse.of(page.map(employeeMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentSalaryStats> getDepartmentSalaryStats() {
        log.info("Computing aggregate department salary statistics");
        return employeeRepository.getSalaryStatsByDepartment();
    }
}
