package ru.tentateursss.appointment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tentateursss.appointment.dto.AppointmentDto;
import ru.tentateursss.appointment.dto.NewAppointmentDto;
import ru.tentateursss.appointment.model.Appointment;
import ru.tentateursss.appointment.repository.AppointmentRepository;
import ru.tentateursss.clinic.model.Clinic;
import ru.tentateursss.clinic.repository.ClinicRepository;
import ru.tentateursss.employee.model.Employee;
import ru.tentateursss.employee.repository.EmployeeRepository;
import ru.tentateursss.enums.AppointmentStatus;
import ru.tentateursss.enums.EmployeeRole;
import ru.tentateursss.exception.DateTimeConflict;
import ru.tentateursss.exception.NotFoundException;
import ru.tentateursss.medicalservice.model.MedicalService;
import ru.tentateursss.medicalservice.repository.MedicalServiceRepository;
import ru.tentateursss.patient.model.Patient;
import ru.tentateursss.patient.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ClinicRepository clinicRepository;

    @Mock
    private MedicalServiceRepository medicalServiceRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Clinic clinic;
    private Patient patient;
    private Employee employee;
    private MedicalService medicalService;
    private Appointment appointment;
    private NewAppointmentDto newAppointmentDto;

    @BeforeEach
    void setUp() {
        clinic = Clinic.builder()
                .id(1L)
                .name("Test Clinic")
                .address("Test Address")
                .phone("+78008008080")
                .email("clinic@test.com")
                .inn("123456789012")
                .build();

        patient = Patient.builder()
                .id(1L)
                .fullName("Ivanov Ivan")
                .phone("+79001234567")
                .email("ivan@mail.ru")
                .birthDate(LocalDate.of(1990, 1, 1))
                .clinic(clinic)
                .build();

        employee = Employee.builder()
                .id(1L)
                .fullName("Petrov Petr")
                .phone("+79007654321")
                .email("petr@mail.ru")
                .role(EmployeeRole.DOCTOR)
                .clinic(clinic)
                .build();

        medicalService = MedicalService.builder()
                .id(1L)
                .title("Consultation")
                .description("Test consultation")
                .cost(1000)
                .durationMinutes(30)
                .clinic(clinic)
                .build();

        appointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .employee(employee)
                .clinic(clinic)
                .dateTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0))
                .medicalService(medicalService)
                .isPaid(false)
                .status(AppointmentStatus.SCHEDULED)
                .build();

        newAppointmentDto = new NewAppointmentDto();
        newAppointmentDto.setPatientId(1L);
        newAppointmentDto.setEmployeeId(1L);
        newAppointmentDto.setClinicId(1L);
        newAppointmentDto.setMedicalServiceId(1L);
        newAppointmentDto.setDateTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
        newAppointmentDto.setIsPaid(false);
        newAppointmentDto.setNotes("Test appointment");
    }

    @Test
    void createAppointmentSuccess() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(medicalServiceRepository.findById(1L)).thenReturn(Optional.of(medicalService));
        when(appointmentRepository.findByEmployeeIdAndDateTimeBetween(anyLong(), any(), any()))
                .thenReturn(List.of());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentDto result = appointmentService.createAppointment(newAppointmentDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(AppointmentStatus.SCHEDULED, result.getStatus());

        verify(clinicRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).findById(1L);
        verify(medicalServiceRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void createAppointmentThrowsNotFoundExceptionWhenClinicNotFound() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            appointmentService.createAppointment(newAppointmentDto);
        });

        verify(clinicRepository, times(1)).findById(1L);
        verify(patientRepository, never()).findById(anyLong());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void createAppointmentThrowsNotFoundExceptionWhenPatientNotFound() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            appointmentService.createAppointment(newAppointmentDto);
        });

        verify(clinicRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void createAppointmentThrowsNotFoundExceptionWhenEmployeeNotFound() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            appointmentService.createAppointment(newAppointmentDto);
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void createAppointmentThrowsNotFoundExceptionWhenMedicalServiceNotFound() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(medicalServiceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            appointmentService.createAppointment(newAppointmentDto);
        });

        verify(medicalServiceRepository, times(1)).findById(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void createAppointmentThrowsDateTimeConflictWhenTimeNotAvailable() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(medicalServiceRepository.findById(1L)).thenReturn(Optional.of(medicalService));
        when(appointmentRepository.findByEmployeeIdAndDateTimeBetween(anyLong(), any(), any()))
                .thenReturn(List.of(appointment));

        assertThrows(DateTimeConflict.class, () -> {
            appointmentService.createAppointment(newAppointmentDto);
        });

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void updateAppointmentSuccess() {
        NewAppointmentDto updateDto = new NewAppointmentDto();
        updateDto.setPatientId(1L);
        updateDto.setEmployeeId(1L);
        updateDto.setClinicId(1L);
        updateDto.setMedicalServiceId(1L);
        updateDto.setDateTime(LocalDateTime.now().plusDays(2).withHour(11).withMinute(0));
        updateDto.setIsPaid(true);
        updateDto.setNotes("Updated notes");

        Appointment updatedAppointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .employee(employee)
                .clinic(clinic)
                .dateTime(LocalDateTime.now().plusDays(2).withHour(11).withMinute(0))
                .medicalService(medicalService)
                .isPaid(true)
                .notes("Updated notes")
                .status(AppointmentStatus.SCHEDULED)
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(medicalServiceRepository.findById(1L)).thenReturn(Optional.of(medicalService));
        when(appointmentRepository.findByEmployeeIdAndDateTimeBetween(anyLong(), any(), any()))
                .thenReturn(List.of());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(updatedAppointment);

        AppointmentDto result = appointmentService.updateAppointment(1L, updateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertTrue(result.getIsPaid());
        assertEquals("Updated notes", result.getNotes());

        verify(appointmentRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).findById(1L);
        verify(clinicRepository, times(1)).findById(1L);
        verify(medicalServiceRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void updateAppointmentThrowsNotFoundExceptionWhenAppointmentNotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            appointmentService.updateAppointment(1L, newAppointmentDto);
        });

        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void updateAppointmentThrowsDateTimeConflictWhenTimeNotAvailable() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(medicalServiceRepository.findById(1L)).thenReturn(Optional.of(medicalService));
        when(appointmentRepository.findByEmployeeIdAndDateTimeBetween(anyLong(), any(), any()))
                .thenReturn(List.of(appointment));

        assertThrows(DateTimeConflict.class, () -> {
            appointmentService.updateAppointment(1L, newAppointmentDto);
        });

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void deleteAppointmentSuccess() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        appointmentService.deleteAppointment(1L);

        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).delete(appointment);
    }

    @Test
    void deleteAppointmentThrowsNotFoundExceptionWhenAppointmentNotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            appointmentService.deleteAppointment(1L);
        });

        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, never()).delete(any(Appointment.class));
    }

    @Test
    void getAppointmentByIdSuccess() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        AppointmentDto result = appointmentService.getAppointmentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(appointmentRepository, times(1)).findById(1L);
    }

    @Test
    void getAppointmentByIdThrowsNotFoundExceptionWhenAppointmentNotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            appointmentService.getAppointmentById(1L);
        });

        verify(appointmentRepository, times(1)).findById(1L);
    }

    @Test
    void getAllAppointmentsSuccess() {
        Appointment appointment = new Appointment();
        Page<Appointment> page = new PageImpl<>(List.of(appointment));

        when(appointmentRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<AppointmentDto> result = appointmentService.getAllAppointments(PageRequest.of(0, 20));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        verify(appointmentRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllAppointmentsReturnsEmptyList() {
        Page<Appointment> emptyPage = new PageImpl<>(List.of());

        when(appointmentRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        Page<AppointmentDto> result = appointmentService.getAllAppointments(PageRequest.of(0, 20));

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());

        verify(appointmentRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAppointmentsByPatientIdSuccess() {
        when(appointmentRepository.findByPatientId(1L)).thenReturn(List.of(appointment));

        List<AppointmentDto> result = appointmentService.getAppointmentsByPatientId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(appointmentRepository, times(1)).findByPatientId(1L);
    }

    @Test
    void getAppointmentsByPatientIdReturnsEmptyList() {
        when(appointmentRepository.findByPatientId(1L)).thenReturn(List.of());

        List<AppointmentDto> result = appointmentService.getAppointmentsByPatientId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(appointmentRepository, times(1)).findByPatientId(1L);
    }

    @Test
    void getAppointmentsByEmployeeIdSuccess() {
        when(appointmentRepository.findByEmployeeId(1L)).thenReturn(List.of(appointment));

        List<AppointmentDto> result = appointmentService.getAppointmentsByEmployeeId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(appointmentRepository, times(1)).findByEmployeeId(1L);
    }

    @Test
    void getAppointmentsByEmployeeIdReturnsEmptyList() {
        when(appointmentRepository.findByEmployeeId(1L)).thenReturn(List.of());

        List<AppointmentDto> result = appointmentService.getAppointmentsByEmployeeId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(appointmentRepository, times(1)).findByEmployeeId(1L);
    }

    @Test
    void getAppointmentsByClinicIdSuccess() {
        when(appointmentRepository.findByClinicId(1L)).thenReturn(List.of(appointment));

        List<AppointmentDto> result = appointmentService.getAppointmentsByClinicId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(appointmentRepository, times(1)).findByClinicId(1L);
    }

    @Test
    void getAppointmentsByMedicalServiceIdSuccess() {
        when(appointmentRepository.findByMedicalServiceId(1L)).thenReturn(List.of(appointment));

        List<AppointmentDto> result = appointmentService.getAppointmentsByMedicalServiceId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(appointmentRepository, times(1)).findByMedicalServiceId(1L);
    }

    @Test
    void getAppointmentsByStatusSuccess() {
        when(appointmentRepository.findByStatus(AppointmentStatus.SCHEDULED))
                .thenReturn(List.of(appointment));

        List<AppointmentDto> result = appointmentService.getAppointmentsByStatus(AppointmentStatus.SCHEDULED);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(AppointmentStatus.SCHEDULED, result.get(0).getStatus());

        verify(appointmentRepository, times(1)).findByStatus(AppointmentStatus.SCHEDULED);
    }

    @Test
    void getAppointmentsByPatientIdAndStatusSuccess() {
        when(appointmentRepository.findByPatientIdAndStatus(1L, AppointmentStatus.SCHEDULED))
                .thenReturn(List.of(appointment));

        List<AppointmentDto> result = appointmentService.getAppointmentsByPatientIdAndStatus(1L, AppointmentStatus.SCHEDULED);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(appointmentRepository, times(1)).findByPatientIdAndStatus(1L, AppointmentStatus.SCHEDULED);
    }

    @Test
    void getAppointmentsByEmployeeIdAndStatusSuccess() {
        when(appointmentRepository.findByEmployeeIdAndStatus(1L, AppointmentStatus.SCHEDULED))
                .thenReturn(List.of(appointment));

        List<AppointmentDto> result = appointmentService.getAppointmentsByEmployeeIdAndStatus(1L, AppointmentStatus.SCHEDULED);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(appointmentRepository, times(1)).findByEmployeeIdAndStatus(1L, AppointmentStatus.SCHEDULED);
    }

    @Test
    void getAppointmentsByDateRangeSuccess() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        when(appointmentRepository.findByDateTimeBetween(start, end))
                .thenReturn(List.of(appointment));

        List<AppointmentDto> result = appointmentService.getAppointmentsByDateRange(start, end);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(appointmentRepository, times(1)).findByDateTimeBetween(start, end);
    }

    @Test
    void getAppointmentsByEmployeeAndDateRangeSuccess() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        when(appointmentRepository.findByEmployeeIdAndDateTimeBetween(1L, start, end))
                .thenReturn(List.of(appointment));

        List<AppointmentDto> result = appointmentService.getAppointmentsByEmployeeAndDateRange(1L, start, end);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(appointmentRepository, times(1)).findByEmployeeIdAndDateTimeBetween(1L, start, end);
    }

    @Test
    void confirmAppointmentSuccess() {
        Appointment confirmedAppointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .employee(employee)
                .clinic(clinic)
                .dateTime(LocalDateTime.now().plusDays(1))
                .medicalService(medicalService)
                .status(AppointmentStatus.CONFIRMED)
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(confirmedAppointment);

        AppointmentDto result = appointmentService.confirmAppointment(1L);

        assertNotNull(result);
        assertEquals(AppointmentStatus.CONFIRMED, result.getStatus());

        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void confirmAppointmentThrowsNotFoundExceptionWhenAppointmentNotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            appointmentService.confirmAppointment(1L);
        });

        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void cancelAppointmentSuccess() {
        Appointment cancelledAppointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .employee(employee)
                .clinic(clinic)
                .dateTime(LocalDateTime.now().plusDays(1))
                .medicalService(medicalService)
                .status(AppointmentStatus.CANCELLED)
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(cancelledAppointment);

        AppointmentDto result = appointmentService.cancelAppointment(1L);

        assertNotNull(result);
        assertEquals(AppointmentStatus.CANCELLED, result.getStatus());

        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void completeAppointmentSuccess() {
        Appointment completedAppointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .employee(employee)
                .clinic(clinic)
                .dateTime(LocalDateTime.now().plusDays(1))
                .medicalService(medicalService)
                .status(AppointmentStatus.COMPLETED)
                .build();

        Appointment confirmedAppointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .employee(employee)
                .clinic(clinic)
                .dateTime(LocalDateTime.now().plusDays(1))
                .medicalService(medicalService)
                .status(AppointmentStatus.CONFIRMED)
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(confirmedAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(completedAppointment);

        AppointmentDto result = appointmentService.completeAppointment(1L);

        assertNotNull(result);
        assertEquals(AppointmentStatus.COMPLETED, result.getStatus());

        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void markAsNoShowSuccess() {
        Appointment noShowAppointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .employee(employee)
                .clinic(clinic)
                .dateTime(LocalDateTime.now().plusDays(1))
                .medicalService(medicalService)
                .status(AppointmentStatus.NO_SHOW)
                .build();

        Appointment confirmedAppointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .employee(employee)
                .clinic(clinic)
                .dateTime(LocalDateTime.now().plusDays(1))
                .medicalService(medicalService)
                .status(AppointmentStatus.CONFIRMED)
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(confirmedAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(noShowAppointment);

        AppointmentDto result = appointmentService.markAsNoShow(1L);

        assertNotNull(result);
        assertEquals(AppointmentStatus.NO_SHOW, result.getStatus());

        verify(appointmentRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void updateStatusThrowsIllegalStateWhenCannotTransitionFromCompleted() {
        Appointment completedAppointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .employee(employee)
                .clinic(clinic)
                .dateTime(LocalDateTime.now().plusDays(1))
                .medicalService(medicalService)
                .status(AppointmentStatus.COMPLETED)
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(completedAppointment));

        assertThrows(IllegalStateException.class, () -> {
            appointmentService.confirmAppointment(1L);
        });

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void updateStatusThrowsIllegalStateWhenCannotTransitionFromCancelled() {
        Appointment cancelledAppointment = Appointment.builder()
                .id(1L)
                .patient(patient)
                .employee(employee)
                .clinic(clinic)
                .dateTime(LocalDateTime.now().plusDays(1))
                .medicalService(medicalService)
                .status(AppointmentStatus.CANCELLED)
                .build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(cancelledAppointment));

        assertThrows(IllegalStateException.class, () -> {
            appointmentService.confirmAppointment(1L);
        });

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void isTimeSlotAvailableReturnsFalseWhenConflictExists() {
        when(appointmentRepository.findByEmployeeIdAndDateTimeBetween(anyLong(), any(), any()))
                .thenReturn(List.of(appointment));
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(medicalServiceRepository.findById(1L)).thenReturn(Optional.of(medicalService));

        assertThrows(DateTimeConflict.class, () -> {
            appointmentService.createAppointment(newAppointmentDto);
        });
    }
}