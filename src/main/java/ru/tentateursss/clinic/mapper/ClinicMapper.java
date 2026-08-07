package ru.tentateursss.clinic.mapper;

import lombok.experimental.UtilityClass;
import ru.tentateursss.clinic.dto.ClinicDto;
import ru.tentateursss.clinic.dto.NewClinicDto;
import ru.tentateursss.clinic.model.Clinic;

@UtilityClass
public class ClinicMapper {

    public ClinicDto toDto(Clinic clinic) {
        if (clinic == null) {
            return null;
        }

        return new ClinicDto(
                clinic.getId(),
                clinic.getClinicCode(),
                clinic.getName(),
                clinic.getAddress(),
                clinic.getPhone(),
                clinic.getEmail(),
                clinic.getInn(),
                clinic.getCreatedAt()
        );
    }

    public Clinic toEntity(NewClinicDto dto) {
        if (dto == null) {
            return null;
        }

        return Clinic.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .inn(dto.getInn())
                .build();
    }

    public void updateEntity(Clinic clinic, NewClinicDto dto) {
        if (clinic == null || dto == null) {
            return;
        }

        clinic.setName(dto.getName());
        clinic.setAddress(dto.getAddress());
        clinic.setPhone(dto.getPhone());
        clinic.setEmail(dto.getEmail());
        clinic.setInn(dto.getInn());
    }
}
