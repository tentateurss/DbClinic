package ru.tentateursss.medicalservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.tentateursss.appointment.model.Appointment;
import ru.tentateursss.appointment.repository.AppointmentRepository;
import ru.tentateursss.clinic.model.Clinic;
import ru.tentateursss.clinic.repository.ClinicRepository;
import ru.tentateursss.employee.model.Employee;
import ru.tentateursss.employee.repository.EmployeeRepository;
import ru.tentateursss.enums.AppointmentStatus;
import ru.tentateursss.enums.EmployeeRole;
import ru.tentateursss.medicalservice.model.MedicalService;
import ru.tentateursss.patient.model.Patient;
import ru.tentateursss.patient.repository.PatientRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class MedicalServiceRepositoryTest {

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

        appointment = appointmentRepository.save(appointment);
    }

    @Test
    void saveMedicalServiceTest(){
        MedicalService savedMedicalService = medicalServiceRepository.save(MedicalService.builder()
                .title("Тестовая услуга 2")
                .description("Тестовая услуга 2")
                .cost(2000)
                .durationMinutes(30)
                .clinic(clinic)
                .build());

        Long id = savedMedicalService.getId();

        assertNotNull(savedMedicalService);
        assertTrue(medicalServiceRepository.existsById(id));
    }

    @Test
    void updateMedicalServiceTest(){
        MedicalService savedMedicalService = medicalServiceRepository.save(medicalService);

        savedMedicalService.setCost(2000);

        MedicalService updatedMedicalService = medicalServiceRepository.save(savedMedicalService);

        assertNotEquals(1000, updatedMedicalService.getCost());
    }

    @Test
    void deleteMedicalServiceTest(){
        MedicalService savedMedicalService = medicalServiceRepository.save(MedicalService.builder()
                .title("Тестовая услуга 2")
                .description("Тестовая услуга 2")
                .cost(2000)
                .durationMinutes(30)
                .clinic(clinic)
                .build());

        Long id = savedMedicalService.getId();

        medicalServiceRepository.deleteById(id);

        assertFalse(medicalServiceRepository.existsById(id));
    }
}
