package ru.tentateursss.appointment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tentateursss.appointment.dto.AppointmentDto;
import ru.tentateursss.appointment.dto.NewAppointmentDto;
import ru.tentateursss.appointment.mapper.AppointmentMapper;
import ru.tentateursss.appointment.model.Appointment;
import ru.tentateursss.appointment.repository.AppointmentRepository;
import ru.tentateursss.clinic.model.Clinic;
import ru.tentateursss.clinic.repository.ClinicRepository;
import ru.tentateursss.employee.model.Employee;
import ru.tentateursss.employee.repository.EmployeeRepository;
import ru.tentateursss.enums.AppointmentStatus;
import ru.tentateursss.exception.DateTimeConflict;
import ru.tentateursss.exception.NotFoundException;
import ru.tentateursss.exception.ValidateException;
import ru.tentateursss.medicalservice.model.MedicalService;
import ru.tentateursss.medicalservice.repository.MedicalServiceRepository;
import ru.tentateursss.patient.model.Patient;
import ru.tentateursss.patient.repository.PatientRepository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final EmployeeRepository employeeRepository;
    private final ClinicRepository clinicRepository;

    @Override
    @Transactional
    public AppointmentDto createAppointment(NewAppointmentDto dto) {
        Clinic clinic = clinicRepository.findById(dto.getClinicId())
                .orElseThrow(() -> new NotFoundException("Клиника не найдена"));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new NotFoundException("Пациент не найден"));

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Работник не найден"));

        MedicalService medicalService = medicalServiceRepository.findById(dto.getMedicalServiceId())
                .orElseThrow(() -> new NotFoundException("Услуга не найдена"));

        LocalDateTime startTime = dto.getDateTime();

        if (!isTimeSlotAvailable(employee.getId(), startTime, medicalService.getDurationMinutes())) {
            throw new DateTimeConflict("Время занято");
        }

        if (!Objects.equals(employee.getClinic().getId(), clinic.getId())) {
            throw new ValidateException("Сотрудник не работает в этой клинике");
        }

        if (!Objects.equals(medicalService.getClinic().getId(), clinic.getId())) {
            throw new ValidateException("Услуга не предоставляется в этой клинике");
        }

        if (!Objects.equals(patient.getClinic().getId(), clinic.getId())) {
            throw new ValidateException("Невозможно записать клиента из другой клиники");
        }

        Appointment newAppointment = AppointmentMapper.toEntity(dto, patient, employee, clinic, medicalService);
        Appointment savedAppointment = appointmentRepository.save(newAppointment);

        log.info("Создана запись для пациента: {}, врач: {}, время: {}",
                patient.getFullName(), employee.getFullName(), startTime);

        return AppointmentMapper.toDto(savedAppointment);
    }

    @Override
    @Transactional
    public AppointmentDto updateAppointment(Long appointmentId, NewAppointmentDto dto) {
        Appointment findAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Запись с ID " + appointmentId + " не найдена"));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new NotFoundException("Пациент с ID " + dto.getPatientId() + " не найден"));

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Сотрудник с ID " + dto.getEmployeeId() + " не найден"));

        Clinic clinic = clinicRepository.findById(dto.getClinicId())
                .orElseThrow(() -> new NotFoundException("Клиника с ID " + dto.getClinicId() + " не найдена"));

        MedicalService medicalService = medicalServiceRepository.findById(dto.getMedicalServiceId())
                .orElseThrow(() -> new NotFoundException("Услуга с ID " + dto.getMedicalServiceId() + " не найдена"));

        LocalDateTime startTime = dto.getDateTime();

        if (!isTimeSlotAvailable(employee.getId(), startTime, medicalService.getDurationMinutes())) {
            throw new DateTimeConflict("Время занято");
        }

        findAppointment.setPatient(patient);
        findAppointment.setEmployee(employee);
        findAppointment.setClinic(clinic);
        findAppointment.setDateTime(startTime);
        findAppointment.setMedicalService(medicalService);
        findAppointment.setIsPaid(dto.getIsPaid() != null && dto.getIsPaid());
        findAppointment.setNotes(dto.getNotes());

        Appointment updatedAppointment = appointmentRepository.save(findAppointment);

        log.info("Обновлена запись с ID: {} для пациента: {}, врач: {}, время: {}",
                updatedAppointment.getId(), patient.getFullName(), employee.getFullName(), startTime);

        return AppointmentMapper.toDto(updatedAppointment);
    }

    @Override
    @Transactional
    public void deleteAppointment(Long appointmentId) {
        Appointment findAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Запись с ID " + appointmentId + " не найдена"));

        appointmentRepository.delete(findAppointment);
        log.info("Обновлена запись с ID: {}", appointmentId);
    }

    @Override
    public AppointmentDto getAppointmentById(Long appointmentId) {
        Appointment findAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Запись с ID " + appointmentId + " не найдена"));

        return AppointmentMapper.toDto(findAppointment);
    }

    @Override
    public Page<AppointmentDto> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable)
                .map(AppointmentMapper::toDto);
    }

    @Override
    public Page<AppointmentDto> getAppointmentsByPatientId(Long patientId, Pageable pageable) {
        return appointmentRepository.findByPatientId(patientId, pageable)
                .map(AppointmentMapper::toDto);
    }

    @Override
    public Page<AppointmentDto> getAppointmentsByEmployeeId(Long employeeId, Pageable pageable) {
        return appointmentRepository.findByEmployeeId(employeeId, pageable)
                .map(AppointmentMapper::toDto);
    }

    @Override
    public Page<AppointmentDto> getAppointmentsByClinicId(Long clinicId, Pageable pageable) {
        return appointmentRepository.findByClinicId(clinicId, pageable)
                .map(AppointmentMapper::toDto);
    }

    @Override
    public Page<AppointmentDto> getAppointmentsByMedicalServiceId(Long medicalServiceId, Pageable pageable) {
        return appointmentRepository.findByMedicalServiceId(medicalServiceId, pageable)
                .map(AppointmentMapper::toDto);
    }

    @Override
    public Page<AppointmentDto> getAppointmentsByStatus(AppointmentStatus status, Pageable pageable) {
        return appointmentRepository.findByStatus(status, pageable)
                .map(AppointmentMapper::toDto);
    }

    @Override
    public Page<AppointmentDto> getAppointmentsByStatuses(List<AppointmentStatus> statuses, Pageable pageable) {
        Page<AppointmentDto> result = appointmentRepository.findByStatusIn(statuses, pageable)
                .map(AppointmentMapper::toDto);
        log.debug("Получение записей со статусами: {} (страница {}, размер {})",
                statuses, pageable.getPageNumber(), pageable.getPageSize());
        return result;
    }

    @Override
    public Page<AppointmentDto> getAppointmentsByPatientIdAndStatus(Long patientId, AppointmentStatus status, Pageable pageable) {
        return appointmentRepository.findByPatientIdAndStatus(patientId, status, pageable)
                .map(AppointmentMapper::toDto);
    }

    @Override
    public Page<AppointmentDto> getAppointmentsByEmployeeIdAndStatus(Long employeeId, AppointmentStatus status, Pageable pageable) {
        return appointmentRepository.findByEmployeeIdAndStatus(employeeId, status, pageable)
                .map(AppointmentMapper::toDto);
    }

    @Override
    public Page<AppointmentDto> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return appointmentRepository.findByDateTimeBetween(start, end, pageable)
                .map(AppointmentMapper::toDto);
    }

    @Override
    public Page<AppointmentDto> getAppointmentsByEmployeeAndDateRange(Long employeeId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return appointmentRepository.findByEmployeeIdAndDateTimeBetween(employeeId, start, end, pageable)
                .map(AppointmentMapper::toDto);
    }

    @Override
    @Transactional
    public AppointmentDto confirmAppointment(Long appointmentId) {
        return updateStatus(appointmentId, AppointmentStatus.CONFIRMED);
    }

    @Override
    @Transactional
    public AppointmentDto cancelAppointment(Long appointmentId) {
        return updateStatus(appointmentId, AppointmentStatus.CANCELLED);
    }

    @Override
    @Transactional
    public AppointmentDto completeAppointment(Long appointmentId) {
        return updateStatus(appointmentId, AppointmentStatus.COMPLETED);
    }

    @Override
    @Transactional
    public AppointmentDto markAsNoShow(Long appointmentId) {
        return updateStatus(appointmentId, AppointmentStatus.NO_SHOW);
    }

    private boolean isTimeSlotAvailable(Long employeeId, LocalDateTime startTime, int durationMinutes) {
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);

        List<Appointment> existingAppointments = appointmentRepository
                .findByEmployeeIdAndDateTimeBetween(
                        employeeId,
                        startTime.minusHours(1),
                        endTime.plusHours(1)
                );

        return existingAppointments.stream().noneMatch(existing -> {
            int existingDuration = existing.getMedicalService() != null
                    ? existing.getMedicalService().getDurationMinutes()
                    : 30;

            LocalDateTime existingStart = existing.getDateTime();
            LocalDateTime existingEnd = existingStart.plusMinutes(existingDuration);

            return startTime.isBefore(existingEnd) && existingStart.isBefore(endTime);
        });
    }

    private AppointmentDto updateStatus(Long appointmentId, AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Запись с ID " + appointmentId + " не найдена"));

        validateStatusTransition(appointment.getStatus(), newStatus);
        appointment.setStatus(newStatus);
        Appointment updated = appointmentRepository.save(appointment);

        log.info("Обновлен статус записи с ID: {} на {}", appointmentId, newStatus);
        return AppointmentMapper.toDto(updated);
    }

    private void validateStatusTransition(AppointmentStatus current, AppointmentStatus next) {
        if (current == next) return;

        switch (current) {
            case COMPLETED, CANCELLED, NO_SHOW ->
                    throw new IllegalStateException("Нельзя изменить статус завершенной записи");
            case SCHEDULED -> {
                if (next != AppointmentStatus.CONFIRMED && next != AppointmentStatus.CANCELLED) {
                    throw new IllegalStateException("SCHEDULED можно изменить только на CONFIRMED или CANCELLED");
                }
            }
            case CONFIRMED -> {
                if (next != AppointmentStatus.COMPLETED && next != AppointmentStatus.CANCELLED && next != AppointmentStatus.NO_SHOW) {
                    throw new IllegalStateException("CONFIRMED можно изменить только на COMPLETED, CANCELLED или NO_SHOW");
                }
            }
        }
    }
}
