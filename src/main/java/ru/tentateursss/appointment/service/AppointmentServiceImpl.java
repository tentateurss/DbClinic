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
import ru.tentateursss.medicalservice.model.MedicalService;
import ru.tentateursss.medicalservice.repository.MedicalServiceRepository;
import ru.tentateursss.patient.model.Patient;
import ru.tentateursss.patient.repository.PatientRepository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<AppointmentDto> getAppointmentsByPatientId(Long patientId) {
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);

        return appointments.stream()
                .map(AppointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getAppointmentsByEmployeeId(Long employeeId) {
        List<Appointment> appointments = appointmentRepository.findByEmployeeId(employeeId);

        return appointments.stream()
                .map(AppointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getAppointmentsByClinicId(Long clinicId) {
        List<Appointment> appointments = appointmentRepository.findByClinicId(clinicId);

        return appointments.stream()
                .map(AppointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getAppointmentsByMedicalServiceId(Long medicalServiceId) {
        List<Appointment> appointments = appointmentRepository.findByMedicalServiceId(medicalServiceId);

        return appointments.stream()
                .map(AppointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getAppointmentsByStatus(AppointmentStatus status) {
        List<Appointment> appointments = appointmentRepository.findByStatus(status);

        return appointments.stream()
                .map(AppointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getAppointmentsByPatientIdAndStatus(Long patientId, AppointmentStatus status) {
        List<Appointment> appointments = appointmentRepository.findByPatientIdAndStatus(patientId, status);

        return appointments.stream()
                .map(AppointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getAppointmentsByEmployeeIdAndStatus(Long employeeId, AppointmentStatus status) {
        List<Appointment> appointments = appointmentRepository.findByEmployeeIdAndStatus(employeeId, status);

        return appointments.stream()
                .map(AppointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Appointment> appointments = appointmentRepository.findByDateTimeBetween(start, end);

        return appointments.stream()
                .map(AppointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getAppointmentsByEmployeeAndDateRange(Long employeeId, LocalDateTime start, LocalDateTime end) {
        List<Appointment> appointments = appointmentRepository.findByEmployeeIdAndDateTimeBetween(employeeId, start, end);

        return appointments.stream()
                .map(AppointmentMapper::toDto)
                .collect(Collectors.toList());
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
