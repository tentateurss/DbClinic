package ru.tentateursss.clinic.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.tentateursss.clinic.dto.ClinicDto;
import ru.tentateursss.clinic.dto.NewClinicDto;

public interface ClinicService {

    ClinicDto createClinic(NewClinicDto dto);

    ClinicDto updateClinic(Long id, NewClinicDto dto);

    void deleteClinic(Long id);

    ClinicDto getClinic(Long id);

    Page<ClinicDto> getAllClinics(Pageable pageable);
}
