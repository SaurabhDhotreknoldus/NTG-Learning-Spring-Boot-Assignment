package com.nashtech.employeeservice.controller;

import com.nashtech.employeeservice.dto.ApiErrorResponse;
import com.nashtech.employeeservice.dto.DepartmentSalaryStats;
import com.nashtech.employeeservice.dto.EmployeeCreateRequest;
import com.nashtech.employeeservice.dto.EmployeeResponse;
import com.nashtech.employeeservice.dto.EmployeeUpdateRequest;
import com.nashtech.employeeservice.dto.PagedResponse;
import com.nashtech.employeeservice.entity.Department;
import com.nashtech.employeeservice.entity.EmployeeStatus;
import com.nashtech.employeeservice.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

/**
 * REST Controller exposing versioned endpoints for Employee Lifecycle Management.
 * Implements CRUD, pagination, derived querying, dynamic custom search, and departmental analytics.
 */
@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "Employee Management", description = "REST APIs for managing corporate employee records")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Operation(summary = "Onboard a new employee", description = "Validates and creates an employee record")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employee successfully created",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or validation failure",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate employee with same email already exists",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody EmployeeCreateRequest request) {
        EmployeeResponse created = employeeService.createEmployee(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Get all employees with pagination and sorting",
            description = "Retrieves paginated list of employees with configurable sorting")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paginated list successfully fetched")
    })
    @GetMapping
    public ResponseEntity<PagedResponse<EmployeeResponse>> getAllEmployees(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "id")
            @RequestParam(defaultValue = "id") String sort,
            @Parameter(description = "Sort direction (asc/desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        return ResponseEntity.ok(employeeService.getAllEmployees(pageable));
    }

    @Operation(summary = "Get employee by ID", description = "Retrieves detailed information of an employee by identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee record found",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(
            @Parameter(description = "Numeric employee ID", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @Operation(summary = "Update employee details", description = "Modifies existing employee fields")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee successfully updated",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update payload",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @Parameter(description = "Numeric employee ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody EmployeeUpdateRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    @Operation(summary = "Delete an employee", description = "Permanently removes an employee from the system")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employee successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @Parameter(description = "Numeric employee ID", example = "1")
            @PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Filter employees by department (Derived Query)",
            description = "Uses Spring Data derived query to filter employees by department")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Filtered list retrieved successfully")
    })
    @GetMapping("/department/{department}")
    public ResponseEntity<PagedResponse<EmployeeResponse>> getEmployeesByDepartment(
            @Parameter(description = "Department name", example = "ENGINEERING")
            @PathVariable Department department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        return ResponseEntity.ok(employeeService.getEmployeesByDepartment(department, pageable));
    }

    @Operation(summary = "Multi-criteria dynamic search (Custom @Query)",
            description = "Performs dynamic filtering using custom JPQL query")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Matching employees retrieved successfully")
    })
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<EmployeeResponse>> searchEmployees(
            @Parameter(description = "Department filter") @RequestParam(required = false) Department department,
            @Parameter(description = "Employment status filter") @RequestParam(required = false) EmployeeStatus status,
            @Parameter(description = "Minimum salary filter") @RequestParam(required = false) BigDecimal minSalary,
            @Parameter(description = "Maximum salary filter") @RequestParam(required = false) BigDecimal maxSalary,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        return ResponseEntity.ok(employeeService.searchEmployees(department, status, minSalary, maxSalary, pageable));
    }

    @Operation(summary = "Department salary analytics (Custom @Query)",
            description = "Executes JPQL aggregation to compute headcounts and salary statistics per department")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Analytical metrics retrieved successfully")
    })
    @GetMapping("/analytics/salary-by-department")
    public ResponseEntity<List<DepartmentSalaryStats>> getDepartmentSalaryStats() {
        return ResponseEntity.ok(employeeService.getDepartmentSalaryStats());
    }
}
