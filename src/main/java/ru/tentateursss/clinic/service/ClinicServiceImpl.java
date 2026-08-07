package ru.tentateursss.clinic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tentateursss.clinic.dto.ClinicDto;
import ru.tentateursss.clinic.dto.NewClinicDto;
import ru.tentateursss.clinic.mapper.ClinicMapper;
import ru.tentateursss.clinic.model.Clinic;
import ru.tentateursss.clinic.repository.ClinicRepository;
import ru.tentateursss.exception.ConflictException;
import ru.tentateursss.exception.NotFoundException;
import ru.tentateursss.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClinicServiceImpl implements ClinicService {

    private final ClinicRepository clinicRepository;

    @Override
    @Transactional
    public ClinicDto createClinic(NewClinicDto dto) {
        if (clinicRepository.existsByInn(dto.getInn())) {
            throw new ConflictException("Клиника с ИНН " + dto.getInn() + " уже существует");
        }

        Clinic clinic = ClinicMapper.toEntity(dto);

        clinic.setClinicCode(Utils.generateTemporaryClinicCode());

        Clinic savedClinic = clinicRepository.save(clinic);

        String clinicCode = Utils.generateClinicCode(savedClinic);
        savedClinic.setClinicCode(clinicCode);

        Clinic updatedClinic = clinicRepository.save(savedClinic);

        log.info("Создана клиника с ID: {}, Code: {}, Name: {}",
                updatedClinic.getId(), updatedClinic.getClinicCode(), updatedClinic.getName());

        return ClinicMapper.toDto(updatedClinic);
    }

    @Override
    @Transactional
    public ClinicDto updateClinic(Long id, NewClinicDto dto) {
        Clinic clinic = clinicRepository.findById(id).orElseThrow(() -> new NotFoundException("Клиника с ID " + id + " не найдена"));

        if (!clinic.getInn().equals(dto.getInn()) && clinicRepository.existsByInn(dto.getInn())) {
            throw new ConflictException("Клиника с ИНН " + dto.getInn() + " уже существует");
        }

        ClinicMapper.updateEntity(clinic, dto);
        Clinic updatedClinic = clinicRepository.save(clinic);
        log.info("Обновлена клиника с ID: {} Name: {}", updatedClinic.getId(), clinic.getName());
        return ClinicMapper.toDto(updatedClinic);
    }

    @Override
    @Transactional
    public void deleteClinic(Long id) {
        Clinic clinic = clinicRepository.findById(id).orElseThrow(() -> new NotFoundException("Клиника с ID " + id + " не найдена"));
        clinicRepository.delete(clinic);
        log.info("Клиника с ID: {} Name: {} удалена", clinic.getId(), clinic.getName());
    }

    @Override
    public ClinicDto getClinic(Long id) {
        Clinic clinic = clinicRepository.findById(id).orElseThrow(() -> new NotFoundException("Клиника с ID " + id + " не найдена"));
        log.info("Получение клиники с ID: {} Name: {}", clinic.getId(), clinic.getName());
        return ClinicMapper.toDto(clinic);
    }

    @Override
    public Page<ClinicDto> getAllClinics(Pageable pageable) {
        Page<Clinic> clinics = clinicRepository.findAll(pageable);
        log.info("Получение всех клиник (страница {}, размер {})",
                pageable.getPageNumber(), pageable.getPageSize());
        return clinics.map(ClinicMapper::toDto);
    }
}
