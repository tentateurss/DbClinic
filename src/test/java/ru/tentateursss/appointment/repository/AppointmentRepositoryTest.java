package ru.tentateursss.appointment.repository;

import ru.tentateursss.appointment.model.Appointment;
import ru.tentateursss.clinic.model.Clinic;
import ru.tentateursss.clinic.repository.ClinicRepository;
import ru.tentateursss.employee.model.Employee;
import ru.tentateursss.employee.repository.EmployeeRepository;
import ru.tentateursss.enums.AppointmentStatus;
import ru.tentateursss.enums.EmployeeRole;
import ru.tentateursss.medicalservice.model.MedicalService;
import ru.tentateursss.medicalservice.repository.MedicalServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.tentateursss.patient.model.Patient;
import ru.tentateursss.patient.repository.PatientRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class AppointmentRepositoryTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private MedicalServiceRepository medicalServiceRepository;

    private Appointment appointment;
    private Patient patient;
    private Employee employee;
    private Clinic clinic;
    private MedicalService medicalService;

    @BeforeEach
    public void setUp(){

        // Создание и сохранение клиники
        clinic = Clinic.builder()
                .clinicCode("Test-001")
                .name("Test")
                .address("Test")
                .phone("+78008008080")
                .email("test@test.com")
                .inn("123456789012")
                .build();

        clinic = clinicRepository.save(clinic);

        // Создание и сохранение пациента
        patient = Patient.builder()
                .fullName("Test")
                .phone("+79009009090")
                .email("test@test.com")
                .birthDate(LocalDate.parse("2026-02-20"))
                .registrationDate(LocalDate.now())
                .medicalCardNumber("MC-001")
                .notes("Тестовый пациент")
                .clinic(clinic)
                .build();

        patient = patientRepository.save(patient);

        // Создание и сохранение работника
        employee = Employee.builder()
                .fullName("Тестовый работник")
                .phone("+79009009090")
                .email("test@test.com")
                .hireDate(LocalDate.parse("2026-02-20"))
                .role(EmployeeRole.DOCTOR)
                .clinic(clinic)
                .specialization("Тестовый")
                .licenseNumber("LIC-001")
                .build();

        employee = employeeRepository.save(employee);

        // Создание и сохранение услуги
        medicalService = MedicalService.builder()
                .title("Тестовая услуга")
                .description("Тестовая услуга")
                .cost(1000)
                .durationMinutes(30)
                .clinic(clinic)
                .build();

        medicalService = medicalServiceRepository.save(medicalService);

        // Создание и сохранение записи
        appointment = Appointment.builder()
                .patient(patient)
                .employee(employee)
                .clinic(clinic)
                .dateTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0))
                .medicalService(medicalService)
                .isPaid(false)
                .notes("Тестовый прием")
                .status(AppointmentStatus.SCHEDULED)
                .build();


    }


    // Тесты стандартных методов
    @Test
    void saveAppointment() {
        Appointment savedAppointment = appointmentRepository.save(appointment);

        assertNotNull(savedAppointment);
        assertNotNull(savedAppointment.getId());
        assertEquals(appointment.getPatient().getId(), savedAppointment.getPatient().getId());
        assertEquals(appointment.getEmployee().getId(), savedAppointment.getEmployee().getId());
        assertEquals(appointment.getClinic().getId(), savedAppointment.getClinic().getId());
        assertEquals(appointment.getDateTime(), savedAppointment.getDateTime());
        assertEquals(AppointmentStatus.SCHEDULED, savedAppointment.getStatus());
        assertNotNull(savedAppointment.getCreatedAt());
    }

    @Test
    void updateAppointment() {
        Appointment savedAppointment = appointmentRepository.save(appointment);

        savedAppointment.setStatus(AppointmentStatus.CONFIRMED);

        Appointment updatedAppointment = appointmentRepository.save(savedAppointment);

        assertNotNull(updatedAppointment);
        assertEquals(AppointmentStatus.CONFIRMED, updatedAppointment.getStatus());
    }

    @Test
    void deleteAppointment() {
        Appointment savedAppointment = appointmentRepository.save(appointment);
        Long appointmentId = savedAppointment.getId();

        appointmentRepository.deleteById(appointmentId);

        assertFalse(appointmentRepository.existsById(appointmentId));
    }

    @Test
    void findAllAppointments() {
        Appointment savedAppointmentOne = appointmentRepository.save(appointment);
        Appointment savedAppointmentTwo = appointmentRepository.save(Appointment.builder()
                .patient(patient)
                .employee(employee)
                .clinic(clinic)
                .dateTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0))
                .medicalService(medicalService)
                .isPaid(false)
                .notes("Тестовый прием")
                .status(AppointmentStatus.SCHEDULED)
                .build());

        List<Appointment> foundAppointments = appointmentRepository.findAll();

        assertNotNull(foundAppointments);
        assertEquals(2, foundAppointments.size());
        assertTrue(foundAppointments.contains(savedAppointmentOne));
        assertTrue(foundAppointments.contains(savedAppointmentTwo));
    }
    
    // Тесты кастомных методов
    @Test
    void findAppointmentById() {
        Appointment savedAppointment = appointmentRepository.save(appointment);

        Appointment foundAppointment = appointmentRepository.findById(savedAppointment.getId()).orElse(null);

        assertNotNull(foundAppointment);
        assertEquals(savedAppointment.getId(), foundAppointment.getId());
        assertEquals(savedAppointment, foundAppointment);
    }

    @Test
    void findAllByPatientId() {
        Appointment savedAppointmentOne = appointmentRepository.save(appointment);
        Appointment savedAppointmentTwo = appointmentRepository.save(Appointment.builder()
                .patient(patient)
                .employee(employee)
                .clinic(clinic)
                .dateTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0))
                .medicalService(medicalService)
                .isPaid(false)
                .notes("Тестовый прием")
                .status(AppointmentStatus.SCHEDULED)
                .build());

        List<Appointment> foundAppointments = appointmentRepository.findByPatientId(patient.getId());

        assertNotNull(foundAppointments);
        assertEquals(2, foundAppointments.size());
        assertTrue(foundAppointments.contains(savedAppointmentOne));
        assertTrue(foundAppointments.contains(savedAppointmentTwo));
    }


}
