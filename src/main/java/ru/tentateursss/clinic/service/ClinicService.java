package ru.tentateursss.clinic.service;

import ru.tentateursss.clinic.dto.ClinicDto;
import ru.tentateursss.clinic.dto.NewClinicDto;

import java.util.List;

public interface ClinicService {

    ClinicDto createClinic(NewClinicDto dto);

    ClinicDto updateClinic(Long id, NewClinicDto dto);

    void deleteClinic(Long id);

    ClinicDto getClinic(Long id);

    List<ClinicDto> getAllClinics();
}
