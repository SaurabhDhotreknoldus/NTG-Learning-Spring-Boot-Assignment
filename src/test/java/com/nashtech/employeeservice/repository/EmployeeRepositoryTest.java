package com.nashtech.employeeservice.repository;

import com.nashtech.employeeservice.config.JpaAuditingConfig;
import com.nashtech.employeeservice.dto.DepartmentSalaryStats;
import com.nashtech.employeeservice.entity.Department;
import com.nashtech.employeeservice.entity.Employee;
import com.nashtech.employeeservice.entity.EmployeeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();

        employee1 = new Employee(
                null,
                "Alex",
                "Turner",
                "alex.turner@nashtechglobal.com",
                Department.ENGINEERING,
                new BigDecimal("95000.00"),
                LocalDate.of(2023, 1, 15),
                EmployeeStatus.ACTIVE
        );

        employee2 = new Employee(
                null,
                "Sarah",
                "Connor",
                "sarah.connor@nashtechglobal.com",
                Department.HR,
                new BigDecimal("75000.00"),
                LocalDate.of(2022, 5, 20),
                EmployeeStatus.ACTIVE
        );

        employeeRepository.saveAll(List.of(employee1, employee2));
    }

    @Test
    @DisplayName("Should detect if an employee exists by email")
    void shouldCheckIfEmailExists() {
        boolean exists = employeeRepository.existsByEmail("alex.turner@nashtechglobal.com");
        boolean notExists = employeeRepository.existsByEmail("unknown@nashtechglobal.com");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should find employee by exact email")
    void shouldFindEmployeeByEmail() {
        Optional<Employee> found = employeeRepository.findByEmail("alex.turner@nashtechglobal.com");

        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Alex");
    }

    @Test
    @DisplayName("Should filter employees by Department using derived query")
    void shouldFilterEmployeesByDepartment() {
        Page<Employee> engEmployees = employeeRepository.findByDepartment(
                Department.ENGINEERING, PageRequest.of(0, 10));

        assertThat(engEmployees.getTotalElements()).isEqualTo(1);
        assertThat(engEmployees.getContent().get(0).getEmail()).isEqualTo("alex.turner@nashtechglobal.com");
    }

    @Test
    @DisplayName("Should dynamically filter using custom JPQL @Query")
    void shouldSearchWithCustomQuery() {
        Page<Employee> results = employeeRepository.searchEmployees(
                Department.ENGINEERING,
                EmployeeStatus.ACTIVE,
                new BigDecimal("90000.00"),
                new BigDecimal("100000.00"),
                PageRequest.of(0, 10)
        );

        assertThat(results.getTotalElements()).isEqualTo(1);
        assertThat(results.getContent().get(0).getLastName()).isEqualTo("Turner");
    }

    @Test
    @DisplayName("Should compute department salary statistics using custom aggregate @Query")
    void shouldComputeDepartmentSalaryStats() {
        List<DepartmentSalaryStats> stats = employeeRepository.getSalaryStatsByDepartment();

        assertThat(stats).hasSize(2);
        DepartmentSalaryStats engStats = stats.stream()
                .filter(s -> s.department() == Department.ENGINEERING)
                .findFirst()
                .orElseThrow();

        assertThat(engStats.employeeCount()).isEqualTo(1);
        assertThat(engStats.averageSalary()).isEqualTo(95000.0);
    }
}
