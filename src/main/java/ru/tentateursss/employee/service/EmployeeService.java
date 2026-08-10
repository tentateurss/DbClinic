package ru.tentateursss.employee.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.tentateursss.employee.dto.EmployeeDto;
import ru.tentateursss.employee.dto.NewEmployeeDto;
import ru.tentateursss.enums.EmployeeRole;

import java.util.List;

public interface EmployeeService {

    EmployeeDto createEmployee(NewEmployeeDto employeeDto);

    EmployeeDto updateEmployee(Long id, NewEmployeeDto dto);

    void deleteEmployee(Long id);

    EmployeeDto getEmployeeById(Long id);

    Page<EmployeeDto> getAllEmployees(Pageable pageable);

    List<EmployeeDto> getEmployeesByClinicId(Long clinicId);

    List<EmployeeDto> getEmployeesByRole(EmployeeRole role);

    List<EmployeeDto> getEmployeesByClinicIdAndRole(Long clinicId, EmployeeRole role);

    List<EmployeeDto> getEmployeesBySpecializationContainingIgnoreCase(String specialization);
}
