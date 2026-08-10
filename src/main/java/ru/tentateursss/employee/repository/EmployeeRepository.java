package ru.tentateursss.employee.repository;

import ru.tentateursss.employee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tentateursss.enums.EmployeeRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByClinicId(Long clinicId);

    List<Employee> findByRole(EmployeeRole role);

    List<Employee> findByClinicIdAndRole(Long clinicId, EmployeeRole role);

    List<Employee> findBySpecializationContainingIgnoreCase(String specialization);

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByLicenseNumber(String licenseNumber);

    boolean existsByClinicId(Long clinicId);
}
