package ru.tentateursss.patient.mapper;

import lombok.experimental.UtilityClass;
import ru.tentateursss.clinic.mapper.ClinicMapper;
import ru.tentateursss.clinic.model.Clinic;
import ru.tentateursss.patient.dto.NewPatientDto;
import ru.tentateursss.patient.dto.PatientDto;
import ru.tentateursss.patient.model.Patient;

@UtilityClass
public class PatientMapper {

    public PatientDto toDto(Patient patient) {
        if (patient == null) {
            return null;
        }

        return new PatientDto(patient.getId(), patient.getFullName(), patient.getPhone(), patient.getEmail(),
                patient.getBirthDate(),patient.getRegistrationDate(), patient.getMedicalCardNumber(), patient.getNotes(),
                ClinicMapper.toDto(patient.getClinic()));
    }

    public Patient toEntity(NewPatientDto patientDto, Clinic clinic) {
        if (patientDto == null) {
            return null;
        }

        return Patient.builder()
                .fullName(patientDto.getFullName())
                .phone(patientDto.getPhone())
                .email(patientDto.getEmail())
                .birthDate(patientDto.getBirthDate())
                .medicalCardNumber(patientDto.getMedicalCardNumber())
                .notes(patientDto.getNotes())
                .clinic(clinic)
                .build();
    }

    public void update(Patient patient, NewPatientDto dto, Clinic clinic) {
        if (patient == null) {
            return;
        }

        patient.setFullName(dto.getFullName());
        patient.setPhone(dto.getPhone());
        patient.setEmail(dto.getEmail());
        patient.setBirthDate(dto.getBirthDate());
        patient.setMedicalCardNumber(dto.getMedicalCardNumber());
        patient.setNotes(dto.getNotes());
        patient.setClinic(clinic);
    }
}
