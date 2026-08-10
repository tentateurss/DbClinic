package ru.tentateursss.patient.service;

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
import ru.tentateursss.exception.ConflictException;
import ru.tentateursss.exception.NotFoundException;
import ru.tentateursss.patient.dto.NewPatientDto;
import ru.tentateursss.patient.dto.PatientDto;
import ru.tentateursss.patient.model.Patient;
import ru.tentateursss.patient.repository.PatientRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private ClinicRepository clinicRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Clinic clinic;
    private Patient patient;
    private NewPatientDto newPatientDto;

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

        patient = Patient.builder()
                .id(1L)
                .fullName("Иванов Иван Иванович")
                .phone("+79001234567")
                .email("ivan@mail.ru")
                .birthDate(LocalDate.of(1990, 1, 1))
                .registrationDate(LocalDate.now())
                .medicalCardNumber("MC-001")
                .notes("Тестовый пациент")
                .clinic(clinic)
                .build();

        newPatientDto = new NewPatientDto();
        newPatientDto.setFullName("Иванов Иван Иванович");
        newPatientDto.setPhone("+79001234567");
        newPatientDto.setEmail("ivan@mail.ru");
        newPatientDto.setBirthDate(LocalDate.of(1990, 1, 1));
        newPatientDto.setMedicalCardNumber("MC-001");
        newPatientDto.setNotes("Тестовый пациент");
        newPatientDto.setClinicId(1L);
    }


    @Test
    void createPatientSuccess() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(patientRepository.existsByPhone(anyString())).thenReturn(false);
        when(patientRepository.existsByEmail(anyString())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientDto result = patientService.createPatient(newPatientDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Иванов Иван Иванович", result.getFullName());
        assertEquals("+79001234567", result.getPhone());

        verify(clinicRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).existsByPhone(anyString());
        verify(patientRepository, times(1)).existsByEmail(anyString());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void createPatientThrowsConflictExceptionWhenPhoneExists() {
        when(patientRepository.existsByPhone(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            patientService.createPatient(newPatientDto);
        });

        verify(patientRepository, times(1)).existsByPhone(anyString());
        verify(patientRepository, never()).existsByEmail(anyString());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void createPatientThrowsConflictExceptionWhenEmailExists() {
        when(patientRepository.existsByPhone(anyString())).thenReturn(false);
        when(patientRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            patientService.createPatient(newPatientDto);
        });

        verify(patientRepository, times(1)).existsByPhone(anyString());
        verify(patientRepository, times(1)).existsByEmail(anyString());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void createPatientThrowsNotFoundExceptionWhenClinicNotFound() {
        when(patientRepository.existsByPhone(anyString())).thenReturn(false);
        when(patientRepository.existsByEmail(anyString())).thenReturn(false);
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            patientService.createPatient(newPatientDto);
        });

        verify(clinicRepository, times(1)).findById(1L);
        verify(patientRepository, never()).save(any(Patient.class));
    }


    @Test
    void updatePatientSuccess() {
        NewPatientDto updateDto = new NewPatientDto();
        updateDto.setFullName("Петров Петр Петрович");
        updateDto.setPhone("+79009876543");
        updateDto.setEmail("petr@mail.ru");
        updateDto.setBirthDate(LocalDate.of(1985, 5, 15));
        updateDto.setMedicalCardNumber("MC-002");
        updateDto.setNotes("Обновленный пациент");
        updateDto.setClinicId(1L);

        Patient updatedPatient = Patient.builder()
                .id(1L)
                .fullName("Петров Петр Петрович")
                .phone("+79009876543")
                .email("petr@mail.ru")
                .birthDate(LocalDate.of(1985, 5, 15))
                .registrationDate(LocalDate.now())
                .medicalCardNumber("MC-002")
                .notes("Обновленный пациент")
                .clinic(clinic)
                .build();

        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.existsByPhone(anyString())).thenReturn(false);
        when(patientRepository.existsByEmail(anyString())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);

        PatientDto result = patientService.updatePatient(1L, updateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Петров Петр Петрович", result.getFullName());
        assertEquals("+79009876543", result.getPhone());

        verify(clinicRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    void updatePatientThrowsNotFoundExceptionWhenPatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            patientService.updatePatient(1L, newPatientDto);
        });

        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void updatePatientThrowsConflictExceptionWhenPhoneExists() {
        NewPatientDto updateDto = new NewPatientDto();
        updateDto.setPhone("+79009876543");
        updateDto.setEmail("petr@mail.ru");
        updateDto.setClinicId(1L);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.existsByPhone(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            patientService.updatePatient(1L, updateDto);
        });

        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).existsByPhone(anyString());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void updatePatientThrowsConflictExceptionWhenEmailExists() {
        NewPatientDto updateDto = new NewPatientDto();
        updateDto.setPhone("+79009876543");
        updateDto.setEmail("petr@mail.ru");
        updateDto.setClinicId(1L);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.existsByPhone(anyString())).thenReturn(false);
        when(patientRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            patientService.updatePatient(1L, updateDto);
        });

        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).existsByPhone(anyString());
        verify(patientRepository, times(1)).existsByEmail(anyString());
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void deletePatientSuccess() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        patientService.deletePatient(1L);

        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).delete(patient);
    }

    @Test
    void deletePatientThrowsNotFoundExceptionWhenPatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            patientService.deletePatient(1L);
        });

        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, never()).delete(any(Patient.class));
    }

    @Test
    void getPatientSuccess() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        PatientDto result = patientService.getPatient(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Иванов Иван Иванович", result.getFullName());

        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void getPatientThrowsNotFoundExceptionWhenPatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            patientService.getPatient(1L);
        });

        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void getAllPatientsSuccess() {
        Page<Patient> page = new PageImpl<>(List.of(patient));

        when(patientRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<PatientDto> result = patientService.getAllPatients(PageRequest.of(0, 20));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Иванов Иван Иванович", result.getContent().get(0).getFullName());

        verify(patientRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllPatientsReturnsEmptyListWhenNoPatients() {
        Page<Patient> emptyPage = new PageImpl<>(List.of());

        when(patientRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        Page<PatientDto> result = patientService.getAllPatients(PageRequest.of(0, 20));

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());

        verify(patientRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getPatientsByClinicIdSuccess() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(patientRepository.findByClinicId(1L)).thenReturn(List.of(patient));

        List<PatientDto> result = patientService.getAllPatientsByClinicId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Иванов Иван Иванович", result.get(0).getFullName());

        verify(clinicRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findByClinicId(1L);
    }

    @Test
    void getPatientsByClinicIdReturnsEmptyListWhenNoPatients() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(patientRepository.findByClinicId(1L)).thenReturn(List.of());

        List<PatientDto> result = patientService.getAllPatientsByClinicId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(clinicRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findByClinicId(1L);
    }

    @Test
    void getPatientsByClinicIdThrowsNotFoundExceptionWhenClinicNotFound() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            patientService.getAllPatientsByClinicId(1L);
        });

        verify(clinicRepository, times(1)).findById(1L);
        verify(patientRepository, never()).findByClinicId(anyLong());
    }

    @Test
    void getPatientByEmailSuccess() {
        when(patientRepository.findByEmail("ivan@mail.ru")).thenReturn(Optional.of(patient));

        PatientDto result = patientService.getPatientByEmail("ivan@mail.ru");

        assertNotNull(result);
        assertEquals("Иванов Иван Иванович", result.getFullName());
        assertEquals("ivan@mail.ru", result.getEmail());

        verify(patientRepository, times(1)).findByEmail("ivan@mail.ru");
    }

    @Test
    void getPatientByEmailThrowsNotFoundException() {
        when(patientRepository.findByEmail("unknown@mail.ru")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            patientService.getPatientByEmail("unknown@mail.ru");
        });

        verify(patientRepository, times(1)).findByEmail("unknown@mail.ru");
    }

    @Test
    void getPatientByPhoneSuccess() {
        when(patientRepository.findByPhone("+79001234567")).thenReturn(Optional.of(patient));

        PatientDto result = patientService.getPatientByPhone("+79001234567");

        assertNotNull(result);
        assertEquals("Иванов Иван Иванович", result.getFullName());
        assertEquals("+79001234567", result.getPhone());

        verify(patientRepository, times(1)).findByPhone("+79001234567");
    }

    @Test
    void getPatientByPhoneThrowsNotFoundException() {
        when(patientRepository.findByPhone("+79999999999")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            patientService.getPatientByPhone("+79999999999");
        });

        verify(patientRepository, times(1)).findByPhone("+79999999999");
    }

    @Test
    void getPatientByFullNameSuccess() {
        when(patientRepository.findByFullNameContainingIgnoreCase("Иванов"))
                .thenReturn(List.of(patient));

        List<PatientDto> result = patientService.getPatientByFullName("Иванов");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Иванов Иван Иванович", result.get(0).getFullName());

        verify(patientRepository, times(1)).findByFullNameContainingIgnoreCase("Иванов");
    }

    @Test
    void getPatientByFullNameReturnsMultiple() {
        Patient patient2 = Patient.builder()
                .id(2L)
                .fullName("Иванов Петр Сергеевич")
                .phone("+79009999999")
                .email("ivanov2@mail.ru")
                .clinic(clinic)
                .build();

        when(patientRepository.findByFullNameContainingIgnoreCase("Иванов"))
                .thenReturn(List.of(patient, patient2));

        List<PatientDto> result = patientService.getPatientByFullName("Иванов");

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(patientRepository, times(1)).findByFullNameContainingIgnoreCase("Иванов");
    }

    @Test
    void getPatientByFullNameReturnsEmptyList() {
        when(patientRepository.findByFullNameContainingIgnoreCase("Неизвестный"))
                .thenReturn(List.of());

        List<PatientDto> result = patientService.getPatientByFullName("Неизвестный");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(patientRepository, times(1)).findByFullNameContainingIgnoreCase("Неизвестный");
    }
}