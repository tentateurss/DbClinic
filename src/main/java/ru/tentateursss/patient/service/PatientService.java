package ru.tentateursss.patient.service;

import ru.tentateursss.patient.dto.NewPatientDto;
import ru.tentateursss.patient.dto.PatientDto;

import java.util.List;

public interface PatientService {

    PatientDto createPatient(NewPatientDto dto);

    PatientDto updatePatient(Long id, NewPatientDto dto);

    void deletePatient(Long id);

    PatientDto getPatient(Long id);

    List<PatientDto> getAllPatients();

    List<PatientDto> getAllPatientsByClinicId(Long clinicId);
}
