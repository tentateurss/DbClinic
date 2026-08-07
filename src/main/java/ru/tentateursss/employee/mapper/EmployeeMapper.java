package ru.tentateursss.employee.mapper;

import lombok.experimental.UtilityClass;
import ru.tentateursss.clinic.mapper.ClinicMapper;
import ru.tentateursss.clinic.model.Clinic;
import ru.tentateursss.employee.dto.EmployeeDto;
import ru.tentateursss.employee.dto.NewEmployeeDto;
import ru.tentateursss.employee.model.Employee;

@UtilityClass
public class EmployeeMapper {

    public EmployeeDto toDto(Employee employee) {
        if (employee == null) return null;
        return new EmployeeDto(employee.getId(), employee.getFullName(), employee.getPhone(), employee.getEmail(),
                employee.getHireDate(), employee.getRole(), ClinicMapper.toDto(employee.getClinic()),
                employee.getSpecialization(), employee.getLicenseNumber());
    }

    public Employee toEntity(NewEmployeeDto employeeDto, Clinic clinic) {
        if (employeeDto == null) return null;

        return Employee.builder()
                .fullName(employeeDto.getFullName())
                .phone(employeeDto.getPhone())
                .email(employeeDto.getEmail())
                .hireDate(employeeDto.getHireDate())
                .role(employeeDto.getRole())
                .specialization(employeeDto.getSpecialization())
                .licenseNumber(employeeDto.getLicenseNumber())
                .clinic(clinic)
                .build();
    }

    public void updateEmployee(Employee employee, NewEmployeeDto dto,Clinic clinic) {
        if (employee == null) return;

        employee.setFullName(dto.getFullName());
        employee.setPhone(dto.getPhone());
        employee.setEmail(dto.getEmail());
        employee.setHireDate(dto.getHireDate());
        employee.setRole(dto.getRole());
        employee.setSpecialization(dto.getSpecialization());
        employee.setLicenseNumber(dto.getLicenseNumber());
        employee.setClinic(clinic);
    }
}
