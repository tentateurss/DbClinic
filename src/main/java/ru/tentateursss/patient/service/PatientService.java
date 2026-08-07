package ru.tentateursss.patient.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.tentateursss.patient.dto.NewPatientDto;
import ru.tentateursss.patient.dto.PatientDto;

import java.util.List;

public interface PatientService {

    PatientDto createPatient(NewPatientDto dto);

    PatientDto updatePatient(Long id, NewPatientDto dto);

    void deletePatient(Long id);

    PatientDto getPatient(Long id);

    Page<PatientDto> getAllPatients(Pageable pageable);

    List<PatientDto> getAllPatientsByClinicId(Long clinicId);
}
