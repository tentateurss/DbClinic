package ru.tentateursss.stats.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tentateursss.appointment.model.Appointment;
import ru.tentateursss.appointment.repository.AppointmentRepository;
import ru.tentateursss.clinic.model.Clinic;
import ru.tentateursss.clinic.repository.ClinicRepository;
import ru.tentateursss.employee.model.Employee;
import ru.tentateursss.employee.repository.EmployeeRepository;
import ru.tentateursss.enums.AppointmentStatus;
import ru.tentateursss.enums.EmployeeRole;
import ru.tentateursss.exception.NotFoundException;
import ru.tentateursss.patient.repository.PatientRepository;
import ru.tentateursss.stats.dto.ClinicSummaryDto;
import ru.tentateursss.stats.dto.DoctorStatsDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ClinicRepository clinicRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private StatsServiceImpl statsService;

    private Clinic clinic;
    private Employee doctor;
    private Employee admin;
    private Appointment scheduled;
    private Appointment completed;

    @BeforeEach
    void setUp() {
        clinic = Clinic.builder().id(1L).name("Тестовая клиника").build();

        doctor = Employee.builder()
                .id(1L).fullName("Петров Петр Петрович")
                .role(EmployeeRole.DOCTOR).clinic(clinic).build();

        admin = Employee.builder()
                .id(2L).fullName("Смирнова Ольга Игоревна")
                .role(EmployeeRole.ADMIN).clinic(clinic).build();

        scheduled = Appointment.builder()
                .id(1L).status(AppointmentStatus.SCHEDULED)
                .dateTime(LocalDateTime.now().plusDays(1)).clinic(clinic).build();

        completed = Appointment.builder()
                .id(2L).status(AppointmentStatus.COMPLETED)
                .dateTime(LocalDateTime.now().minusDays(1)).clinic(clinic).build();
    }

    @Test
    void getEmployeeCountByRoleSuccess() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(employeeRepository.findByClinicId(1L)).thenReturn(List.of(doctor, admin));

        Map<EmployeeRole, Long> result = statsService.getEmployeeCountByRole(1L);

        assertEquals(1L, result.get(EmployeeRole.DOCTOR));
        assertEquals(1L, result.get(EmployeeRole.ADMIN));

        verify(clinicRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).findByClinicId(1L);
    }

    @Test
    void getEmployeeCountByRoleThrowsNotFound() {
        when(clinicRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> statsService.getEmployeeCountByRole(999L));

        verify(clinicRepository, times(1)).findById(999L);
        verify(employeeRepository, never()).findByClinicId(anyLong());
    }

    @Test
    void getAppointmentCountByStatusSuccess() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(appointmentRepository.findByClinicId(1L)).thenReturn(List.of(scheduled, completed));

        Map<AppointmentStatus, Long> result = statsService.getAppointmentCountByStatus(1L);

        assertEquals(1L, result.get(AppointmentStatus.SCHEDULED));
        assertEquals(1L, result.get(AppointmentStatus.COMPLETED));

        verify(clinicRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).findByClinicId(1L);
    }

    @Test
    void getAppointmentCountByStatusThrowsNotFound() {
        when(clinicRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> statsService.getAppointmentCountByStatus(999L));

        verify(clinicRepository, times(1)).findById(999L);
        verify(appointmentRepository, never()).findByClinicId(anyLong());
    }

    @Test
    void getDoctorStatsSuccess() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(employeeRepository.findByClinicId(1L)).thenReturn(List.of(doctor, admin));
        when(appointmentRepository.countByEmployeeId(1L)).thenReturn(5L);

        List<DoctorStatsDto> result = statsService.getDoctorStats(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getDoctorId());
        assertEquals("Петров Петр Петрович", result.get(0).getFullName());
        assertEquals(5L, result.get(0).getTotalAppointments());

        verify(clinicRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).findByClinicId(1L);
        verify(appointmentRepository, times(1)).countByEmployeeId(1L);
    }

    @Test
    void getDoctorStatsThrowsNotFound() {
        when(clinicRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> statsService.getDoctorStats(999L));

        verify(clinicRepository, times(1)).findById(999L);
        verify(employeeRepository, never()).findByClinicId(anyLong());
    }

    @Test
    void getPatientCountByClinicIdSuccess() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(patientRepository.countByClinicId(1L)).thenReturn(10L);

        Long result = statsService.getPatientCountByClinicId(1L);

        assertEquals(10L, result);

        verify(clinicRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).countByClinicId(1L);
    }

    @Test
    void getPatientCountByClinicIdThrowsNotFound() {
        when(clinicRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> statsService.getPatientCountByClinicId(999L));

        verify(clinicRepository, times(1)).findById(999L);
        verify(patientRepository, never()).countByClinicId(anyLong());
    }

    @Test
    void getClinicSummarySuccess() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(employeeRepository.findByClinicId(1L)).thenReturn(List.of(doctor, admin));
        when(patientRepository.countByClinicId(1L)).thenReturn(10L);
        when(appointmentRepository.findByClinicId(1L)).thenReturn(List.of(scheduled, completed));

        ClinicSummaryDto result = statsService.getClinicSummary(1L);

        assertEquals(2L, result.getTotalEmployees());
        assertEquals(1L, result.getTotalDoctors());
        assertEquals(10L, result.getTotalPatients());
        assertEquals(2L, result.getTotalAppointments());
        assertEquals(1L, result.getScheduledAppointments());
        assertEquals(1L, result.getCompletedAppointments());
        assertEquals(0L, result.getCancelledAppointments());
        assertEquals(0L, result.getNoShowAppointments());

        verify(clinicRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).findByClinicId(1L);
        verify(patientRepository, times(1)).countByClinicId(1L);
        verify(appointmentRepository, times(1)).findByClinicId(1L);
    }

    @Test
    void getClinicSummaryThrowsNotFound() {
        when(clinicRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> statsService.getClinicSummary(999L));

        verify(clinicRepository, times(1)).findById(999L);
    }

    @Test
    void getOverallSummarySuccess() {
        when(employeeRepository.findAll()).thenReturn(List.of(doctor, admin));
        when(patientRepository.count()).thenReturn(20L);
        when(appointmentRepository.findAll()).thenReturn(List.of(scheduled, completed));

        ClinicSummaryDto result = statsService.getOverallSummary();

        assertEquals(2L, result.getTotalEmployees());
        assertEquals(1L, result.getTotalDoctors());
        assertEquals(20L, result.getTotalPatients());
        assertEquals(2L, result.getTotalAppointments());
        assertEquals(1L, result.getScheduledAppointments());
        assertEquals(1L, result.getCompletedAppointments());

        verify(employeeRepository, times(1)).findAll();
        verify(patientRepository, times(1)).count();
        verify(appointmentRepository, times(1)).findAll();
    }
}