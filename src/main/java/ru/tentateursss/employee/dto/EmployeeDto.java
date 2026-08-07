package ru.tentateursss.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tentateursss.clinic.dto.ClinicDto;
import ru.tentateursss.enums.EmployeeRole;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {
    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private LocalDate hireDate;
    private EmployeeRole role;
    private ClinicDto clinic;
    private String specialization;
    private String licenseNumber;
}
