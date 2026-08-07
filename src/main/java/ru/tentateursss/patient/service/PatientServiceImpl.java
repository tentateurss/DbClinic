package ru.tentateursss.patient.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tentateursss.clinic.model.Clinic;
import ru.tentateursss.clinic.repository.ClinicRepository;
import ru.tentateursss.exception.ConflictException;
import ru.tentateursss.exception.NotFoundException;
import ru.tentateursss.patient.dto.NewPatientDto;
import ru.tentateursss.patient.dto.PatientDto;
import ru.tentateursss.patient.mapper.PatientMapper;
import ru.tentateursss.patient.model.Patient;
import ru.tentateursss.patient.repository.PatientRepository;
import ru.tentateursss.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final ClinicRepository clinicRepository;

    @Override
    @Transactional
    public PatientDto createPatient(NewPatientDto dto) {
        if (patientRepository.existsByPhone(dto.getPhone())) {
            throw new ConflictException("Пациент с телефоном " + dto.getPhone() + " уже существует");
        }

        if (patientRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Пациент с email " + dto.getEmail() + " уже существует");
        }

        Clinic clinic = clinicRepository.findById(dto.getClinicId())
                .orElseThrow(() -> new NotFoundException("Клиника с ID " + dto.getClinicId() + " не найдена"));

        Patient newPatient = PatientMapper.toEntity(dto, clinic);
        newPatient.setFullName(Utils.splitBio(dto.getFullName()));
        Patient savedPatient = patientRepository.save(newPatient);

        log.info("Создан пациент: {} для клиники: {}", savedPatient.getFullName(), clinic.getName());
        return PatientMapper.toDto(savedPatient);
    }

    @Override
    @Transactional
    public PatientDto updatePatient(Long id, NewPatientDto dto) {
        Patient findPatient = patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пациент с ID " + id + " не найден"));

        if (!findPatient.getPhone().equals(dto.getPhone()) &&
                patientRepository.existsByPhone(dto.getPhone())) {
            throw new ConflictException("Пациент с телефоном " + dto.getPhone() + " уже существует");
        }

        if (!findPatient.getEmail().equals(dto.getEmail()) &&
                patientRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Пациент с email " + dto.getEmail() + " уже существует");
        }

        Clinic clinic = clinicRepository.findById(dto.getClinicId())
                .orElseThrow(() -> new NotFoundException("Клиника с ID " + dto.getClinicId() + " не найдена"));

        PatientMapper.update(findPatient, dto, clinic);
        findPatient.setFullName(Utils.splitBio(dto.getFullName()));
        Patient updatedPatient = patientRepository.save(findPatient);

        log.info("Обновлен пациент с ID: {}, FullName: {}", updatedPatient.getId(), updatedPatient.getFullName());
        return PatientMapper.toDto(updatedPatient);
    }

    @Override
    @Transactional
    public void deletePatient(Long id) {
        Patient findPatient = patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пациент с ID " + id + " не найден"));

        patientRepository.delete(findPatient);
        log.info("Удален пациент с ID: {}, FullName: {}", id, findPatient.getFullName());
    }

    @Override
    public PatientDto getPatient(Long id) {
        Patient findPatient = patientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пациент с ID " + id + " не найден"));

        log.debug("Получен пациент с ID: {}", id);
        return PatientMapper.toDto(findPatient);
    }

    @Override
    public Page<PatientDto> getAllPatients(Pageable pageable) {
        Page<Patient> patients = patientRepository.findAll(pageable);
        log.debug("Получение всех пациентов (страница {}, размер {})",
                pageable.getPageNumber(), pageable.getPageSize());
        return patients.map(PatientMapper::toDto);
    }

    @Override
    public List<PatientDto> getAllPatientsByClinicId(Long clinicId) {
        clinicRepository.findById(clinicId)
                .orElseThrow(() -> new NotFoundException("Клиника с ID " + clinicId + " не найдена"));

        List<Patient> patients = patientRepository.findByClinicId(clinicId);

        log.debug("Найдено {} пациентов для клиники с ID: {}", patients.size(), clinicId);
        return patients.stream()
                .map(PatientMapper::toDto)
                .collect(Collectors.toList());
    }
}