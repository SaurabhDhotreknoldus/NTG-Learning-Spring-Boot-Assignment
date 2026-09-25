package com.nashtech.employeeservice.repository;

import com.nashtech.employeeservice.dto.DepartmentSalaryStats;
import com.nashtech.employeeservice.entity.Department;
import com.nashtech.employeeservice.entity.Employee;
import com.nashtech.employeeservice.entity.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Employee entity.
 * Includes derived queries and custom JPQL queries.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Derived Query: Check if employee with given email already exists
    boolean existsByEmail(String email);

    // Derived Query: Lookup employee by exact corporate email
    Optional<Employee> findByEmail(String email);

    // Derived Query: Filter employees by department with pagination
    Page<Employee> findByDepartment(Department department, Pageable pageable);

    // Derived Query: Filter employees by status with pagination
    Page<Employee> findByStatus(EmployeeStatus status, Pageable pageable);

    // Derived Query: Case-insensitive search by last name
    Page<Employee> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    // Custom @Query Feature 1: Multi-criteria dynamic filtering with pagination
    @Query("""
            SELECT e FROM Employee e
            WHERE (:department IS NULL OR e.department = :department)
              AND (:status IS NULL OR e.status = :status)
              AND (:minSalary IS NULL OR e.salary >= :minSalary)
              AND (:maxSalary IS NULL OR e.salary <= :maxSalary)
            """)
    Page<Employee> searchEmployees(
            @Param("department") Department department,
            @Param("status") EmployeeStatus status,
            @Param("minSalary") BigDecimal minSalary,
            @Param("maxSalary") BigDecimal maxSalary,
            Pageable pageable
    );

    // Custom @Query Feature 2: Analytical aggregation calculating department statistics
    @Query("""
            SELECT new com.nashtech.employeeservice.dto.DepartmentSalaryStats(
                e.department,
                COUNT(e),
                AVG(e.salary),
                MAX(e.salary),
                MIN(e.salary)
            )
            FROM Employee e
            GROUP BY e.department
            ORDER BY e.department ASC
            """)
    List<DepartmentSalaryStats> getSalaryStatsByDepartment();
}
