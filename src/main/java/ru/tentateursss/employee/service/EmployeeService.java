package ru.tentateursss.employee.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.tentateursss.employee.dto.EmployeeDto;
import ru.tentateursss.employee.dto.NewEmployeeDto;
import ru.tentateursss.enums.EmployeeRole;

public interface EmployeeService {

    EmployeeDto createEmployee(NewEmployeeDto employeeDto);

    EmployeeDto updateEmployee(Long id, NewEmployeeDto dto);

    void deleteEmployee(Long id);

    EmployeeDto getEmployeeById(Long id);

    Page<EmployeeDto> getAllEmployees(Pageable pageable);

    Page<EmployeeDto> getEmployeesByClinicId(Long clinicId, Pageable pageable);

    Page<EmployeeDto> getEmployeesByRole(EmployeeRole role, Pageable pageable);

    Page<EmployeeDto> getEmployeesByClinicIdAndRole(Long clinicId, EmployeeRole role, Pageable pageable);

    Page<EmployeeDto> getEmployeesBySpecializationContainingIgnoreCase(String specialization, Pageable pageable);
}