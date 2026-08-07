package ru.tentateursss.medicalservice.mapper;

import lombok.experimental.UtilityClass;
import ru.tentateursss.clinic.mapper.ClinicMapper;
import ru.tentateursss.clinic.model.Clinic;
import ru.tentateursss.medicalservice.dto.MedicalServiceDto;
import ru.tentateursss.medicalservice.dto.NewMedicalServiceDto;
import ru.tentateursss.medicalservice.model.MedicalService;

@UtilityClass
public class MedicalServiceMapper {

    public MedicalServiceDto toDto(MedicalService medicalService) {
        if (medicalService == null) return null;

        return new MedicalServiceDto(
                medicalService.getId(),
                medicalService.getTitle(),
                medicalService.getDescription(),
                medicalService.getCost(),
                medicalService.getDurationMinutes(),
                ClinicMapper.toDto(medicalService.getClinic())
        );
    }

    public MedicalService toEntity(NewMedicalServiceDto dto, Clinic clinic) {
        if (dto == null) return null;

        return MedicalService.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .cost(dto.getCost())
                .durationMinutes(dto.getDurationMinutes())
                .clinic(clinic)
                .build();
    }

    public void updateEntity(MedicalService medicalService, NewMedicalServiceDto dto, Clinic clinic) {
        if (dto == null || medicalService == null) return;

        medicalService.setTitle(dto.getTitle());
        medicalService.setDescription(dto.getDescription());
        medicalService.setCost(dto.getCost());
        medicalService.setDurationMinutes(dto.getDurationMinutes());
        medicalService.setClinic(clinic);
    }
}
