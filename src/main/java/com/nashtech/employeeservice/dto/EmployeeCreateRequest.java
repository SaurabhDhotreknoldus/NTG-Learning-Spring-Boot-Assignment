package com.nashtech.employeeservice.dto;

import com.nashtech.employeeservice.entity.Department;
import com.nashtech.employeeservice.entity.EmployeeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request payload for onboarding a new employee")
public record EmployeeCreateRequest(
        @Schema(description = "First name of the employee", example = "John")
        @NotBlank(message = "First name is mandatory")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @Schema(description = "Last name of the employee", example = "Doe")
        @NotBlank(message = "Last name is mandatory")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        @Schema(description = "Corporate email address (unique)", example = "john.doe@nashtechglobal.com")
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email must be a valid email address format")
        String email,

        @Schema(description = "Assigned department", example = "ENGINEERING")
        @NotNull(message = "Department is mandatory")
        Department department,

        @Schema(description = "Annual salary", example = "85000.00")
        @NotNull(message = "Salary is mandatory")
        @Positive(message = "Salary must be strictly positive")
        @DecimalMin(value = "1000.00", message = "Salary cannot be lower than minimum base wage threshold of 1000.00")
        BigDecimal salary,

        @Schema(description = "Date of hiring (cannot be future date)", example = "2024-01-15")
        @NotNull(message = "Hire date is mandatory")
        @PastOrPresent(message = "Hire date cannot be in the future")
        LocalDate hireDate,

        @Schema(description = "Initial employment status", example = "ACTIVE")
        @NotNull(message = "Status is mandatory")
        EmployeeStatus status
) {
}
