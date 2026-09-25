package com.nashtech.employeeservice.mapper;

import com.nashtech.employeeservice.dto.EmployeeCreateRequest;
import com.nashtech.employeeservice.dto.EmployeeResponse;
import com.nashtech.employeeservice.dto.EmployeeUpdateRequest;
import com.nashtech.employeeservice.entity.Employee;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Component responsible for transforming between domain entities and REST DTOs.
 */
@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeCreateRequest request) {
        if (request == null) {
            return null;
        }
        Employee employee = new Employee();
        employee.setFirstName(request.firstName().trim());
        employee.setLastName(request.lastName().trim());
        employee.setEmail(request.email().trim().toLowerCase());
        employee.setDepartment(request.department());
        employee.setSalary(request.salary());
        employee.setHireDate(request.hireDate());
        employee.setStatus(request.status());
        return employee;
    }

    public EmployeeResponse toResponse(Employee employee) {
        if (employee == null) {
            return null;
        }
        String fullName = (employee.getFirstName() != null ? employee.getFirstName() : "")
                + " "
                + (employee.getLastName() != null ? employee.getLastName() : "");

        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                fullName.trim(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getSalary(),
                employee.getHireDate(),
                employee.getStatus(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }

    public List<EmployeeResponse> toResponseList(List<Employee> employees) {
        if (employees == null) {
            return List.of();
        }
        return employees.stream()
                .map(this::toResponse)
                .toList();
    }

    public void updateEntityFromDto(EmployeeUpdateRequest request, Employee employee) {
        if (request == null || employee == null) {
            return;
        }
        if (request.firstName() != null && !request.firstName().isBlank()) {
            employee.setFirstName(request.firstName().trim());
        }
        if (request.lastName() != null && !request.lastName().isBlank()) {
            employee.setLastName(request.lastName().trim());
        }
        if (request.department() != null) {
            employee.setDepartment(request.department());
        }
        if (request.salary() != null) {
            employee.setSalary(request.salary());
        }
        if (request.status() != null) {
            employee.setStatus(request.status());
        }
    }
}
