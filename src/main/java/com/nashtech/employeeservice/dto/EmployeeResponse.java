package com.nashtech.employeeservice.dto;

import com.nashtech.employeeservice.entity.Department;
import com.nashtech.employeeservice.entity.EmployeeStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Response payload representing employee details")
public record EmployeeResponse(
        @Schema(description = "Unique employee identifier", example = "1")
        Long id,

        @Schema(description = "First name", example = "John")
        String firstName,

        @Schema(description = "Last name", example = "Doe")
        String lastName,

        @Schema(description = "Full combined name", example = "John Doe")
        String fullName,

        @Schema(description = "Corporate email", example = "john.doe@nashtechglobal.com")
        String email,

        @Schema(description = "Department name", example = "ENGINEERING")
        Department department,

        @Schema(description = "Current salary", example = "85000.00")
        BigDecimal salary,

        @Schema(description = "Date hired", example = "2024-01-15")
        LocalDate hireDate,

        @Schema(description = "Employment status", example = "ACTIVE")
        EmployeeStatus status,

        @Schema(description = "Record creation audit timestamp", example = "2024-01-15T10:00:00")
        LocalDateTime createdAt,

        @Schema(description = "Record last modification audit timestamp", example = "2024-01-15T10:00:00")
        LocalDateTime updatedAt
) {
}
