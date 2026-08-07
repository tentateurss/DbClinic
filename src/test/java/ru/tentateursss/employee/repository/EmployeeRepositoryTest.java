package ru.tentateursss.employee.repository;

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
import ru.tentateursss.enums.AppointmentStatus;
import ru.tentateursss.enums.EmployeeRole;
import ru.tentateursss.medicalservice.model.MedicalService;
import ru.tentateursss.medicalservice.repository.MedicalServiceRepository;
import ru.tentateursss.patient.model.Patient;
import ru.tentateursss.patient.repository.PatientRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class EmployeeRepositoryTest {

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
    public void setUp() {

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
    void saveEmployeeTest() {
        Employee savedEmployee = employeeRepository.save(employee);

        assertNotNull(savedEmployee);
        assertNotNull(savedEmployee.getId());
    }

    @Test
    void updateEmployeeTest() {
        Employee savedEmployee = employeeRepository.save(employee);

        assertNotNull(savedEmployee);

        savedEmployee.setRole(EmployeeRole.ADMIN);

        Employee updateEmployee = employeeRepository.save(savedEmployee);

        assertEquals(EmployeeRole.ADMIN, updateEmployee.getRole());
    }

    @Test
    void deleteEmployeeTest() {
        Employee savedEmployee = employeeRepository.save(Employee.builder()
                .fullName("Тестовый работник 2")
                .phone("+79009009091")
                .email("test@testt.com")
                .hireDate(LocalDate.parse("2026-02-20"))
                .role(EmployeeRole.ADMIN)
                .clinic(clinic)
                .specialization("Тестовый")
                .licenseNumber("LIC-002")
                .build());

        Long id = savedEmployee.getId();

        assertNotNull(savedEmployee);
        employeeRepository.deleteById(id);

        assertFalse(employeeRepository.existsById(id));
    }

    @Test
    void findEmployeeByRoleTest() {
        List<Employee> findEmployee = employeeRepository.findByRole(EmployeeRole.DOCTOR);

        assertNotNull(findEmployee);
    }
}
