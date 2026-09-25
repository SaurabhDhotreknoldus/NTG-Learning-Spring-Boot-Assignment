package com.nashtech.employeeservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.employeeservice.dto.DepartmentSalaryStats;
import com.nashtech.employeeservice.dto.EmployeeCreateRequest;
import com.nashtech.employeeservice.dto.EmployeeResponse;
import com.nashtech.employeeservice.dto.EmployeeUpdateRequest;
import com.nashtech.employeeservice.dto.PagedResponse;
import com.nashtech.employeeservice.entity.Department;
import com.nashtech.employeeservice.entity.EmployeeStatus;
import com.nashtech.employeeservice.exception.DuplicateResourceException;
import com.nashtech.employeeservice.exception.ResourceNotFoundException;
import com.nashtech.employeeservice.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @MockitoBean
    private EmployeeService employeeService;

    private final EmployeeResponse sampleResponse = new EmployeeResponse(
            1L,
            "James",
            "Gosling",
            "James Gosling",
            "james.gosling@java.net",
            Department.ENGINEERING,
            new BigDecimal("150000.00"),
            LocalDate.of(2021, 6, 1),
            EmployeeStatus.ACTIVE,
            LocalDateTime.now(),
            LocalDateTime.now()
    );

    @Test
    @DisplayName("POST /api/v1/employees - Should create employee and return 201 Created with Location header")
    void shouldCreateEmployee() throws Exception {
        EmployeeCreateRequest request = new EmployeeCreateRequest(
                "James",
                "Gosling",
                "james.gosling@java.net",
                Department.ENGINEERING,
                new BigDecimal("150000.00"),
                LocalDate.of(2021, 6, 1),
                EmployeeStatus.ACTIVE
        );

        when(employeeService.createEmployee(any(EmployeeCreateRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/employees/1")))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("james.gosling@java.net")))
                .andExpect(jsonPath("$.fullName", is("James Gosling")));
    }

    @Test
    @DisplayName("POST /api/v1/employees - Should return 400 Bad Request when validation fails")
    void shouldReturnBadRequestOnValidationFailure() throws Exception {
        // Missing firstName, invalid email, negative salary
        EmployeeCreateRequest invalidRequest = new EmployeeCreateRequest(
                "",
                "Gosling",
                "not-an-email",
                Department.ENGINEERING,
                new BigDecimal("-50.00"),
                LocalDate.of(2021, 6, 1),
                EmployeeStatus.ACTIVE
        );

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.validationErrors.firstName", notNullValue()))
                .andExpect(jsonPath("$.validationErrors.email", notNullValue()))
                .andExpect(jsonPath("$.validationErrors.salary", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/employees - Should return 409 Conflict when email already exists")
    void shouldReturnConflictWhenEmailExists() throws Exception {
        EmployeeCreateRequest request = new EmployeeCreateRequest(
                "James",
                "Gosling",
                "james.gosling@java.net",
                Department.ENGINEERING,
                new BigDecimal("150000.00"),
                LocalDate.of(2021, 6, 1),
                EmployeeStatus.ACTIVE
        );

        when(employeeService.createEmployee(any(EmployeeCreateRequest.class)))
                .thenThrow(new DuplicateResourceException("Employee", "email", request.email()));

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @DisplayName("GET /api/v1/employees - Should return 200 OK with paginated list")
    void shouldGetAllEmployees() throws Exception {
        PagedResponse<EmployeeResponse> pagedResponse = new PagedResponse<>(
                List.of(sampleResponse), 0, 10, 1, 1, true
        );

        when(employeeService.getAllEmployees(any(Pageable.class))).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v1/employees")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.isLast", is(true)));
    }

    @Test
    @DisplayName("GET /api/v1/employees/{id} - Should return 200 OK when found")
    void shouldGetEmployeeById() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("James")));
    }

    @Test
    @DisplayName("GET /api/v1/employees/{id} - Should return 404 Not Found when ID does not exist")
    void shouldReturnNotFoundWhenEmployeeMissing() throws Exception {
        when(employeeService.getEmployeeById(99L))
                .thenThrow(new ResourceNotFoundException("Employee", "id", 99L));

        mockMvc.perform(get("/api/v1/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Employee not found with id: '99'")));
    }

    @Test
    @DisplayName("PUT /api/v1/employees/{id} - Should update employee successfully")
    void shouldUpdateEmployee() throws Exception {
        EmployeeUpdateRequest updateRequest = new EmployeeUpdateRequest(
                "Jimmy", null, Department.PRODUCT, new BigDecimal("160000.00"), EmployeeStatus.ACTIVE
        );

        when(employeeService.updateEmployee(eq(1L), any(EmployeeUpdateRequest.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(put("/api/v1/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("DELETE /api/v1/employees/{id} - Should return 204 No Content on success")
    void shouldDeleteEmployee() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/v1/employees/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/employees/{id} - Should return 404 Not Found when entity does not exist")
    void shouldReturnNotFoundOnDeleteMissing() throws Exception {
        doThrow(new ResourceNotFoundException("Employee", "id", 99L))
                .when(employeeService).deleteEmployee(99L);

        mockMvc.perform(delete("/api/v1/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @DisplayName("GET /api/v1/employees/analytics/salary-by-department - Should return analytics")
    void shouldGetDepartmentSalaryStats() throws Exception {
        DepartmentSalaryStats stats = new DepartmentSalaryStats(
                Department.ENGINEERING, 5L, 120000.0, new BigDecimal("150000.00"), new BigDecimal("90000.00")
        );

        when(employeeService.getDepartmentSalaryStats()).thenReturn(List.of(stats));

        mockMvc.perform(get("/api/v1/employees/analytics/salary-by-department"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].department", is("ENGINEERING")))
                .andExpect(jsonPath("$[0].employeeCount", is(5)));
    }
}
