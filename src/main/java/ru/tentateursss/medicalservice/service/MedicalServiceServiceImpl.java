package ru.tentateursss.medicalservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tentateursss.clinic.model.Clinic;
import ru.tentateursss.clinic.repository.ClinicRepository;
import ru.tentateursss.exception.NotFoundException;
import ru.tentateursss.medicalservice.dto.MedicalServiceDto;
import ru.tentateursss.medicalservice.dto.NewMedicalServiceDto;
import ru.tentateursss.medicalservice.mapper.MedicalServiceMapper;
import ru.tentateursss.medicalservice.model.MedicalService;
import ru.tentateursss.medicalservice.repository.MedicalServiceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicalServiceServiceImpl implements MedicalServiceService {

    private final MedicalServiceRepository medicalServiceRepository;
    private final ClinicRepository clinicRepository;

    @Override
    @Transactional
    public MedicalServiceDto createMedicalService(NewMedicalServiceDto dto) {
        Clinic clinic = clinicRepository.findById(dto.getClinicId())
                .orElseThrow(() -> new NotFoundException("Клиника с ID " + dto.getClinicId() + " не найдена"));

        MedicalService entity = MedicalServiceMapper.toEntity(dto, clinic);
        MedicalService savedEntity = medicalServiceRepository.save(entity);

        log.info("Создана услуга: {} для клиники: {}", savedEntity.getTitle(), clinic.getName());
        return MedicalServiceMapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    public MedicalServiceDto updateMedicalService(Long id, NewMedicalServiceDto dto) {
        Clinic clinic = clinicRepository.findById(dto.getClinicId())
                .orElseThrow(() -> new NotFoundException("Клиника с ID " + dto.getClinicId() + " не найдена"));

        MedicalService find = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Услуга с ID " + id + " не найдена"));

        MedicalServiceMapper.updateEntity(find, dto, clinic);
        MedicalService updated = medicalServiceRepository.save(find);

        log.info("Обновлена услуга с ID: {}, Title: {}", updated.getId(), updated.getTitle());
        return MedicalServiceMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteMedicalService(Long id) {
        MedicalService find = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Услуга с ID " + id + " не найдена"));

        medicalServiceRepository.delete(find);
        log.info("Удалена услуга с ID: {}, Title: {}", find.getId(), find.getTitle());
    }

    @Override
    public List<MedicalServiceDto> findMedicalServiceByClinicId(Long clinicId) {
        clinicRepository.findById(clinicId)
                .orElseThrow(() -> new NotFoundException("Клиника с ID " + clinicId + " не найдена"));

        List<MedicalService> list = medicalServiceRepository.findByClinicId(clinicId);

        log.debug("Найдено {} услуг для клиники с ID: {}", list.size(), clinicId);
        return list.stream()
                .map(MedicalServiceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalServiceDto> findAllMedicalService() {
        List<MedicalService> list = medicalServiceRepository.findAll();

        log.debug("Найдено {} услуг", list.size());
        return list.stream()
                .map(MedicalServiceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public MedicalServiceDto findMedicalServiceById(Long id) {
        MedicalService find = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Услуга с ID " + id + " не найдена"));

        log.debug("Получена услуга с ID: {}", id);
        return MedicalServiceMapper.toDto(find);
    }
}