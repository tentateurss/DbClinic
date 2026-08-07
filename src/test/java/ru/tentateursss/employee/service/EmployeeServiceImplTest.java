package ru.tentateursss.employee.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.tentateursss.clinic.model.Clinic;
import ru.tentateursss.clinic.repository.ClinicRepository;
import ru.tentateursss.employee.dto.EmployeeDto;
import ru.tentateursss.employee.dto.NewEmployeeDto;
import ru.tentateursss.employee.model.Employee;
import ru.tentateursss.employee.repository.EmployeeRepository;
import ru.tentateursss.enums.EmployeeRole;
import ru.tentateursss.exception.ConflictException;
import ru.tentateursss.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ClinicRepository clinicRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Clinic clinic;
    private Employee employee;
    private NewEmployeeDto newEmployeeDto;

    @BeforeEach
    void setUp() {
        clinic = Clinic.builder()
                .id(1L)
                .name("Тестовая клиника")
                .address("ул. Ленина, 1")
                .phone("+78008008080")
                .email("info@clinic.ru")
                .inn("123456789012")
                .build();

        employee = Employee.builder()
                .id(1L)
                .fullName("Петров Петр Петрович")
                .phone("+79001234567")
                .email("petrov@mail.ru")
                .hireDate(LocalDate.now())
                .role(EmployeeRole.DOCTOR)
                .clinic(clinic)
                .specialization("Терапевт")
                .licenseNumber("LIC-001")
                .build();

        newEmployeeDto = new NewEmployeeDto();
        newEmployeeDto.setFullName("Петров Петр Петрович");
        newEmployeeDto.setPhone("+79001234567");
        newEmployeeDto.setEmail("petrov@mail.ru");
        newEmployeeDto.setHireDate(LocalDate.now());
        newEmployeeDto.setRole(EmployeeRole.DOCTOR);
        newEmployeeDto.setClinicId(1L);
        newEmployeeDto.setSpecialization("Терапевт");
        newEmployeeDto.setLicenseNumber("LIC-001");
    }

    @Test
    void createEmployeeSuccess() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.existsByPhone(anyString())).thenReturn(false);
        when(employeeRepository.existsByLicenseNumber(anyString())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDto result = employeeService.createEmployee(newEmployeeDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Петров Петр Петрович", result.getFullName());
        assertEquals("+79001234567", result.getPhone());
        assertEquals(EmployeeRole.DOCTOR, result.getRole());

        verify(clinicRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).existsByEmail(anyString());
        verify(employeeRepository, times(1)).existsByPhone(anyString());
        verify(employeeRepository, times(1)).existsByLicenseNumber(anyString());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void createEmployeeThrowsConflictExceptionWhenEmailExists() {
        when(employeeRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            employeeService.createEmployee(newEmployeeDto);
        });

        verify(employeeRepository, times(1)).existsByEmail(anyString());
        verify(employeeRepository, never()).existsByPhone(anyString());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void createEmployeeThrowsConflictExceptionWhenPhoneExists() {
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.existsByPhone(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            employeeService.createEmployee(newEmployeeDto);
        });

        verify(employeeRepository, times(1)).existsByEmail(anyString());
        verify(employeeRepository, times(1)).existsByPhone(anyString());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void createEmployeeThrowsConflictExceptionWhenLicenseNumberExists() {
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.existsByPhone(anyString())).thenReturn(false);
        when(employeeRepository.existsByLicenseNumber(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            employeeService.createEmployee(newEmployeeDto);
        });

        verify(employeeRepository, times(1)).existsByEmail(anyString());
        verify(employeeRepository, times(1)).existsByPhone(anyString());
        verify(employeeRepository, times(1)).existsByLicenseNumber(anyString());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void createEmployeeThrowsNotFoundExceptionWhenClinicNotFound() {
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.existsByPhone(anyString())).thenReturn(false);
        when(employeeRepository.existsByLicenseNumber(anyString())).thenReturn(false);
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            employeeService.createEmployee(newEmployeeDto);
        });

        verify(clinicRepository, times(1)).findById(1L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void updateEmployeeSuccess() {
        NewEmployeeDto updateDto = new NewEmployeeDto();
        updateDto.setFullName("Иванов Иван Иванович");
        updateDto.setPhone("+79009876543");
        updateDto.setEmail("ivanov@mail.ru");
        updateDto.setHireDate(LocalDate.now());
        updateDto.setRole(EmployeeRole.DOCTOR);
        updateDto.setClinicId(1L);
        updateDto.setSpecialization("Хирург");
        updateDto.setLicenseNumber("LIC-002");

        Employee updatedEmployee = Employee.builder()
                .id(1L)
                .fullName("Иванов Иван Иванович")
                .phone("+79009876543")
                .email("ivanov@mail.ru")
                .hireDate(LocalDate.now())
                .role(EmployeeRole.DOCTOR)
                .clinic(clinic)
                .specialization("Хирург")
                .licenseNumber("LIC-002")
                .build();

        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByPhone(anyString())).thenReturn(false);
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.existsByLicenseNumber(anyString())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        EmployeeDto result = employeeService.updateEmployee(1L, updateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Иванов Иван Иванович", result.getFullName());
        assertEquals("+79009876543", result.getPhone());

        verify(clinicRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void updateEmployeeThrowsNotFoundExceptionWhenEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            employeeService.updateEmployee(1L, newEmployeeDto);
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void updateEmployeeThrowsConflictExceptionWhenPhoneExists() {
        NewEmployeeDto updateDto = new NewEmployeeDto();
        updateDto.setPhone("+79009876543");
        updateDto.setEmail("petrov@mail.ru");
        updateDto.setClinicId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByPhone(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            employeeService.updateEmployee(1L, updateDto);
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).existsByPhone(anyString());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void updateEmployeeThrowsConflictExceptionWhenEmailExists() {
        NewEmployeeDto updateDto = new NewEmployeeDto();
        updateDto.setPhone("+79009876543");
        updateDto.setEmail("ivanov@mail.ru");
        updateDto.setClinicId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByPhone(anyString())).thenReturn(false);
        when(employeeRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            employeeService.updateEmployee(1L, updateDto);
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).existsByPhone(anyString());
        verify(employeeRepository, times(1)).existsByEmail(anyString());
        verify(employeeRepository, never()).save(any(Employee.class));
    }


    @Test
    void deleteEmployeeSuccess() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    void deleteEmployeeThrowsNotFoundExceptionWhenEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            employeeService.deleteEmployee(1L);
        });

        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, never()).delete(any(Employee.class));
    }

    @Test
    void getEmployeeByIdSuccess() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeDto result = employeeService.getEmployeeById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Петров Петр Петрович", result.getFullName());

        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void getEmployeeByIdThrowsNotFoundExceptionWhenEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            employeeService.getEmployeeById(1L);
        });

        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void getAllEmployeesSuccess() {
        Page<Employee> page = new PageImpl<>(List.of(employee));

        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<EmployeeDto> result = employeeService.getAllEmployees(PageRequest.of(0, 20));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Петров Петр Петрович", result.getContent().get(0).getFullName());

        verify(employeeRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllEmployeesReturnsEmptyListWhenNoEmployees() {
        Page<Employee> emptyPage = new PageImpl<>(List.of());

        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        Page<EmployeeDto> result = employeeService.getAllEmployees(PageRequest.of(0, 20));

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());

        verify(employeeRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getEmployeesByClinicIdSuccess() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(employeeRepository.findByClinicId(1L)).thenReturn(List.of(employee));

        List<EmployeeDto> result = employeeService.getEmployeesByClinicId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Петров Петр Петрович", result.get(0).getFullName());

        verify(clinicRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).findByClinicId(1L);
    }

    @Test
    void getEmployeesByClinicIdReturnsEmptyListWhenNoEmployees() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(employeeRepository.findByClinicId(1L)).thenReturn(List.of());

        List<EmployeeDto> result = employeeService.getEmployeesByClinicId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(clinicRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).findByClinicId(1L);
    }

    @Test
    void getEmployeesByClinicIdThrowsNotFoundExceptionWhenClinicNotFound() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            employeeService.getEmployeesByClinicId(1L);
        });

        verify(clinicRepository, times(1)).findById(1L);
        verify(employeeRepository, never()).findByClinicId(anyLong());
    }

    @Test
    void getEmployeesByRoleSuccess() {
        when(employeeRepository.findByRole(EmployeeRole.DOCTOR)).thenReturn(List.of(employee));

        List<EmployeeDto> result = employeeService.getEmployeesByRole(EmployeeRole.DOCTOR);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(EmployeeRole.DOCTOR, result.get(0).getRole());

        verify(employeeRepository, times(1)).findByRole(EmployeeRole.DOCTOR);
    }

    @Test
    void getEmployeesByRoleReturnsEmptyListWhenNoEmployeesWithRole() {
        when(employeeRepository.findByRole(EmployeeRole.ADMIN)).thenReturn(List.of());

        List<EmployeeDto> result = employeeService.getEmployeesByRole(EmployeeRole.ADMIN);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(employeeRepository, times(1)).findByRole(EmployeeRole.ADMIN);
    }

    @Test
    void getEmployeesByClinicIdAndRoleSuccess() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(employeeRepository.findByClinicIdAndRole(1L, EmployeeRole.DOCTOR))
                .thenReturn(List.of(employee));

        List<EmployeeDto> result = employeeService.getEmployeesByClinicIdAndRole(1L, EmployeeRole.DOCTOR);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Петров Петр Петрович", result.get(0).getFullName());
        assertEquals(EmployeeRole.DOCTOR, result.get(0).getRole());

        verify(clinicRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).findByClinicIdAndRole(1L, EmployeeRole.DOCTOR);
    }

    @Test
    void getEmployeesByClinicIdAndRoleReturnsEmptyListWhenNoEmployees() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(employeeRepository.findByClinicIdAndRole(1L, EmployeeRole.ADMIN))
                .thenReturn(List.of());

        List<EmployeeDto> result = employeeService.getEmployeesByClinicIdAndRole(1L, EmployeeRole.ADMIN);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(clinicRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).findByClinicIdAndRole(1L, EmployeeRole.ADMIN);
    }

    @Test
    void getEmployeesByClinicIdAndRoleThrowsNotFoundExceptionWhenClinicNotFound() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            employeeService.getEmployeesByClinicIdAndRole(1L, EmployeeRole.DOCTOR);
        });

        verify(clinicRepository, times(1)).findById(1L);
        verify(employeeRepository, never()).findByClinicIdAndRole(anyLong(), any(EmployeeRole.class));
    }
}