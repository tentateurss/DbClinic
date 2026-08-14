package ru.tentateursss.appointment.service;

import org.springframework.data.domain.Page;
import ru.tentateursss.appointment.dto.AppointmentDto;
import ru.tentateursss.appointment.dto.NewAppointmentDto;
import ru.tentateursss.enums.AppointmentStatus;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {

    AppointmentDto createAppointment(NewAppointmentDto dto);

    AppointmentDto updateAppointment(Long appointmentId, NewAppointmentDto dto);

    void deleteAppointment(Long appointmentId);

    AppointmentDto getAppointmentById(Long appointmentId);

    Page<AppointmentDto> getAllAppointments(Pageable pageable);

    Page<AppointmentDto> getAppointmentsByPatientId(Long patientId, Pageable pageable);

    Page<AppointmentDto> getAppointmentsByEmployeeId(Long employeeId, Pageable pageable);

    Page<AppointmentDto> getAppointmentsByClinicId(Long clinicId, Pageable pageable);

    Page<AppointmentDto> getAppointmentsByMedicalServiceId(Long medicalServiceId, Pageable pageable);

    Page<AppointmentDto> getAppointmentsByStatus(AppointmentStatus status, Pageable pageable);

    Page<AppointmentDto> getAppointmentsByStatuses(List<AppointmentStatus> statuses, Pageable pageable);

    Page<AppointmentDto> getAppointmentsByPatientIdAndStatus(Long patientId, AppointmentStatus status, Pageable pageable);

    Page<AppointmentDto> getAppointmentsByEmployeeIdAndStatus(Long employeeId, AppointmentStatus status, Pageable pageable);

    Page<AppointmentDto> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<AppointmentDto> getAppointmentsByEmployeeAndDateRange(Long employeeId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    AppointmentDto confirmAppointment(Long appointmentId);

    AppointmentDto cancelAppointment(Long appointmentId);

    AppointmentDto completeAppointment(Long appointmentId);

    AppointmentDto markAsNoShow(Long appointmentId);
}