package com.nashtech.employeeservice.dto;

import com.nashtech.employeeservice.entity.Department;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Aggregated salary statistics for a department")
public record DepartmentSalaryStats(
        @Schema(description = "Department name", example = "ENGINEERING")
        Department department,

        @Schema(description = "Total employee count in department", example = "12")
        Long employeeCount,

        @Schema(description = "Average department salary", example = "92500.50")
        Double averageSalary,

        @Schema(description = "Highest salary in department", example = "140000.00")
        BigDecimal highestSalary,

        @Schema(description = "Lowest salary in department", example = "60000.00")
        BigDecimal lowestSalary
) {
}
