package ru.tentateursss.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tentateursss.clinic.dto.ClinicDto;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDto {
    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private LocalDate birthDate;
    private LocalDate registrationDate;
    private String medicalCardNumber;
    private String notes;
    private ClinicDto clinic;
}
