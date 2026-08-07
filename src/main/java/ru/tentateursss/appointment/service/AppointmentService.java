package ru.tentateursss.appointment.service;

import ru.tentateursss.appointment.dto.AppointmentDto;
import ru.tentateursss.appointment.dto.NewAppointmentDto;
import ru.tentateursss.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {

    AppointmentDto createAppointment(NewAppointmentDto dto);

    AppointmentDto updateAppointment(Long appointmentId, NewAppointmentDto dto);

    void deleteAppointment(Long appointmentId);

    AppointmentDto getAppointmentById(Long appointmentId);

    List<AppointmentDto> getAllAppointments();

    List<AppointmentDto> getAppointmentsByPatientId(Long patientId);

    List<AppointmentDto> getAppointmentsByEmployeeId(Long employeeId);

    List<AppointmentDto> getAppointmentsByClinicId(Long clinicId);

    List<AppointmentDto> getAppointmentsByMedicalServiceId(Long medicalServiceId);

    List<AppointmentDto> getAppointmentsByStatus(AppointmentStatus status);

    List<AppointmentDto> getAppointmentsByPatientIdAndStatus(Long patientId, AppointmentStatus status);

    List<AppointmentDto> getAppointmentsByEmployeeIdAndStatus(Long employeeId, AppointmentStatus status);

    List<AppointmentDto> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end);

    List<AppointmentDto> getAppointmentsByEmployeeAndDateRange(Long employeeId, LocalDateTime start, LocalDateTime end);

    AppointmentDto confirmAppointment(Long appointmentId);

    AppointmentDto cancelAppointment(Long appointmentId);

    AppointmentDto completeAppointment(Long appointmentId);

    AppointmentDto markAsNoShow(Long appointmentId);
}