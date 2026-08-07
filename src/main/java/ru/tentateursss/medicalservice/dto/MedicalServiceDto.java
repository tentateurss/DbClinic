package ru.tentateursss.medicalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tentateursss.clinic.dto.ClinicDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicalServiceDto {
    private Long id;
    private String title;
    private String description;
    private int cost;
    private Integer durationMinutes;
    private ClinicDto clinic;
}