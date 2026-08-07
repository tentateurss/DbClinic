package ru.tentateursss.medicalservice.service;

import ru.tentateursss.medicalservice.dto.MedicalServiceDto;
import ru.tentateursss.medicalservice.dto.NewMedicalServiceDto;

import java.util.List;

public interface MedicalServiceService {

    MedicalServiceDto createMedicalService(NewMedicalServiceDto dto);

    MedicalServiceDto updateMedicalService(Long id, NewMedicalServiceDto dto);

    void deleteMedicalService(Long id);

    List<MedicalServiceDto> findMedicalServiceByClinicId(Long clinicId);

    List<MedicalServiceDto> findAllMedicalService();

    MedicalServiceDto findMedicalServiceById(Long id);
}
