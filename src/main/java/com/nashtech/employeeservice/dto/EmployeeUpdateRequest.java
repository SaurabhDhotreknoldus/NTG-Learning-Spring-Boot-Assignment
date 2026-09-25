package com.nashtech.employeeservice.dto;

import com.nashtech.employeeservice.entity.Department;
import com.nashtech.employeeservice.entity.EmployeeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Request payload for updating an existing employee")
public record EmployeeUpdateRequest(
        @Schema(description = "Updated first name", example = "Jonathan")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @Schema(description = "Updated last name", example = "Doe")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        @Schema(description = "Updated department", example = "PRODUCT")
        Department department,

        @Schema(description = "Updated salary", example = "95000.00")
        @Positive(message = "Salary must be strictly positive")
        @DecimalMin(value = "1000.00", message = "Salary cannot be lower than minimum base wage threshold of 1000.00")
        BigDecimal salary,

        @Schema(description = "Updated employment status", example = "PROBATION")
        EmployeeStatus status
) {
}
