package com.nashtech.employeeservice.service;

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
import com.nashtech.employeeservice.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private EmployeeMapper employeeMapper = new EmployeeMapper();

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        employee = new Employee(
                1L,
                "Robert",
                "Martin",
                "uncle.bob@cleancode.org",
                Department.ENGINEERING,
                new BigDecimal("120000.00"),
                LocalDate.of(2020, 1, 1),
                EmployeeStatus.ACTIVE
        );

        createRequest = new EmployeeCreateRequest(
                "Robert",
                "Martin",
                "uncle.bob@cleancode.org",
                Department.ENGINEERING,
                new BigDecimal("120000.00"),
                LocalDate.of(2020, 1, 1),
                EmployeeStatus.ACTIVE
        );
    }

    @Test
    @DisplayName("Should create employee successfully when email is unique")
    void shouldCreateEmployeeSuccessfully() {
        when(employeeRepository.existsByEmail("uncle.bob@cleancode.org")).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponse response = employeeService.createEmployee(createRequest);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("uncle.bob@cleancode.org");
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void shouldThrowDuplicateResourceException() {
        when(employeeRepository.existsByEmail("uncle.bob@cleancode.org")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.createEmployee(createRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Employee already exists with email: 'uncle.bob@cleancode.org'");

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should fetch employee by ID when present")
    void shouldGetEmployeeById() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResponse response = employeeService.getEmployeeById(1L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.firstName()).isEqualTo("Robert");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when ID does not exist")
    void shouldThrowExceptionWhenEmployeeNotFound() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Employee not found with id: '999'");
    }

    @Test
    @DisplayName("Should update existing employee")
    void shouldUpdateEmployee() {
        EmployeeUpdateRequest updateRequest = new EmployeeUpdateRequest(
                "Bob",
                null,
                Department.PRODUCT,
                new BigDecimal("130000.00"),
                EmployeeStatus.ACTIVE
        );

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponse response = employeeService.updateEmployee(1L, updateRequest);

        assertThat(response).isNotNull();
        verify(employeeRepository).save(employee);
    }

    @Test
    @DisplayName("Should delete employee successfully")
    void shouldDeleteEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).delete(employee);
    }

    @Test
    @DisplayName("Should return paginated employees")
    void shouldReturnPaginatedEmployees() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> page = new PageImpl<>(List.of(employee), pageable, 1);
        when(employeeRepository.findAll(pageable)).thenReturn(page);

        PagedResponse<EmployeeResponse> response = employeeService.getAllEmployees(pageable);

        assertThat(response.content()).hasSize(1);
        assertThat(response.totalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return department salary statistics")
    void shouldReturnDepartmentSalaryStats() {
        DepartmentSalaryStats stats = new DepartmentSalaryStats(
                Department.ENGINEERING, 1L, 120000.0, new BigDecimal("120000.00"), new BigDecimal("120000.00")
        );
        when(employeeRepository.getSalaryStatsByDepartment()).thenReturn(List.of(stats));

        List<DepartmentSalaryStats> result = employeeService.getDepartmentSalaryStats();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).department()).isEqualTo(Department.ENGINEERING);
    }
}
