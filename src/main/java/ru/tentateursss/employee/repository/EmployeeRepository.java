package ru.tentateursss.employee.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.tentateursss.employee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tentateursss.enums.EmployeeRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Page<Employee> findByClinicId(Long clinicId, Pageable pageable);

    List<Employee> findByClinicId(Long clinicId);

    Page<Employee> findByRole(EmployeeRole role, Pageable pageable);

    List<Employee> findByRole(EmployeeRole role);

    Page<Employee> findByClinicIdAndRole(Long clinicId, EmployeeRole role, Pageable pageable);

    Page<Employee> findBySpecializationContainingIgnoreCase(String specialization, Pageable pageable);

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByLicenseNumber(String licenseNumber);

    boolean existsByClinicId(Long clinicId);
}