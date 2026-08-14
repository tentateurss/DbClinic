package ru.tentateursss.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.tentateursss.patient.dto.NewPatientDto;
import ru.tentateursss.patient.dto.PatientDto;

public interface PatientService {

    PatientDto createPatient(NewPatientDto dto);

    PatientDto updatePatient(Long id, NewPatientDto dto);

    void deletePatient(Long id);

    PatientDto getPatient(Long id);

    PatientDto getPatientByEmail(String email);

    PatientDto getPatientByPhone(String phone);

    Page<PatientDto> getPatientByFullName(String fullName, Pageable pageable);

    Page<PatientDto> getAllPatients(Pageable pageable);

    Page<PatientDto> getAllPatientsByClinicId(Long clinicId, Pageable pageable);
}
