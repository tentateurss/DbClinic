package ru.tentateursss.appointment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tentateursss.clinic.dto.ClinicDto;
import ru.tentateursss.employee.dto.EmployeeDto;
import ru.tentateursss.enums.AppointmentStatus;
import ru.tentateursss.medicalservice.dto.MedicalServiceDto;
import ru.tentateursss.patient.dto.PatientDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {
    private Long id;
    private PatientDto patient;
    private EmployeeDto employee;
    private ClinicDto clinic;
    private LocalDateTime dateTime;
    private MedicalServiceDto medicalService;
    private Boolean isPaid;
    private String notes;
    private AppointmentStatus status;
    private LocalDateTime createdAt;
    private Integer cost;
}