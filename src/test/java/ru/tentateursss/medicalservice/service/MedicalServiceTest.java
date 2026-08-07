package ru.tentateursss.medicalservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tentateursss.clinic.model.Clinic;
import ru.tentateursss.clinic.repository.ClinicRepository;
import ru.tentateursss.exception.NotFoundException;
import ru.tentateursss.medicalservice.dto.MedicalServiceDto;
import ru.tentateursss.medicalservice.dto.NewMedicalServiceDto;
import ru.tentateursss.medicalservice.model.MedicalService;
import ru.tentateursss.medicalservice.repository.MedicalServiceRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicalServiceTest {

    @Mock
    MedicalServiceRepository medicalServiceRepository;

    @Mock
    private ClinicRepository clinicRepository;

    @InjectMocks
    MedicalServiceServiceImpl medicalServiceService;

    private Clinic clinic;
    private MedicalService medicalService;
    private NewMedicalServiceDto newMedicalServiceDto;

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

        medicalService = MedicalService.builder()
                .id(1L)
                .title("Тестовая услуга")
                .description("Описание тестовой услуги")
                .cost(1000)
                .clinic(clinic)
                .build();

        newMedicalServiceDto = new NewMedicalServiceDto();
        newMedicalServiceDto.setTitle("Тестовая услуга");
        newMedicalServiceDto.setDescription("Описание тестовой услуги");
        newMedicalServiceDto.setCost(1000);
        newMedicalServiceDto.setClinicId(1L);
    }

    @Test
    void createMedicalService() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(medicalServiceRepository.save(any(MedicalService.class))).thenReturn(medicalService);

        MedicalServiceDto result = medicalServiceService.createMedicalService(newMedicalServiceDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Тестовая услуга", result.getTitle());
        assertEquals(1000, result.getCost());

        verify(clinicRepository, times(1)).findById(1L);
        verify(medicalServiceRepository, times(1)).save(any(MedicalService.class));
    }

    @Test
    void createMedicalServiceThrowsNotFoundExceptionWhenClinicNotFound() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            medicalServiceService.createMedicalService(newMedicalServiceDto);
        });

        verify(clinicRepository, times(1)).findById(1L);
        verify(medicalServiceRepository, never()).save(any(MedicalService.class));
    }

    @Test
    void updateMedicalService_Success() {
        NewMedicalServiceDto updateDto = new NewMedicalServiceDto();
        updateDto.setTitle("Обновленная услуга");
        updateDto.setDescription("Новое описание");
        updateDto.setCost(1500);
        updateDto.setClinicId(1L);

        MedicalService updatedMedicalService = MedicalService.builder()
                .id(1L)
                .title("Обновленная услуга")
                .description("Новое описание")
                .cost(1500)
                .clinic(clinic)
                .build();

        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(medicalServiceRepository.findById(1L)).thenReturn(Optional.of(medicalService));
        when(medicalServiceRepository.save(any(MedicalService.class))).thenReturn(updatedMedicalService);

        MedicalServiceDto result = medicalServiceService.updateMedicalService(1L, updateDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Обновленная услуга", result.getTitle());
        assertEquals(1500, result.getCost());

        verify(clinicRepository, times(1)).findById(1L);
        verify(medicalServiceRepository, times(1)).findById(1L);
        verify(medicalServiceRepository, times(1)).save(any(MedicalService.class));
    }

    @Test
    void updateMedicalServiceThrowsNotFoundExceptionWhenClinicNotFound() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(medicalServiceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            medicalServiceService.updateMedicalService(1L, newMedicalServiceDto);
        });

        verify(clinicRepository, times(1)).findById(1L);
        verify(medicalServiceRepository, times(1)).findById(1L);
        verify(medicalServiceRepository, never()).save(any(MedicalService.class));
    }

    @Test
    void updateMedicalService_ThrowsNotFoundException_WhenClinicNotFound() {
        // Arrange
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            medicalServiceService.updateMedicalService(1L, newMedicalServiceDto);
        });

        verify(clinicRepository, times(1)).findById(1L);
        verify(medicalServiceRepository, never()).findById(anyLong());
        verify(medicalServiceRepository, never()).save(any(MedicalService.class));
    }

    @Test
    void deleteMedicalServiceSuccess() {
        when(medicalServiceRepository.findById(1L)).thenReturn(Optional.of(medicalService));

        medicalServiceService.deleteMedicalService(1L);

        verify(medicalServiceRepository, times(1)).findById(1L);
        verify(medicalServiceRepository, times(1)).delete(medicalService);
    }

    @Test
    void deleteMedicalServiceThrowsNotFoundExceptionWhenServiceNotFound() {
        when(medicalServiceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            medicalServiceService.deleteMedicalService(1L);
        });

        verify(medicalServiceRepository, times(1)).findById(1L);
        verify(medicalServiceRepository, never()).delete(any(MedicalService.class));
    }

    @Test
    void findMedicalServiceByClinicIdSuccess() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(medicalServiceRepository.findByClinicId(1L)).thenReturn(List.of(medicalService));

        List<MedicalServiceDto> result = medicalServiceService.findMedicalServiceByClinicId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Тестовая услуга", result.get(0).getTitle());

        verify(clinicRepository, times(1)).findById(1L);
        verify(medicalServiceRepository, times(1)).findByClinicId(1L);
    }

    @Test
    void findMedicalServiceByClinicIdReturnsEmptyListWhenNoServices() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(medicalServiceRepository.findByClinicId(1L)).thenReturn(List.of());

        List<MedicalServiceDto> result = medicalServiceService.findMedicalServiceByClinicId(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(clinicRepository, times(1)).findById(1L);
        verify(medicalServiceRepository, times(1)).findByClinicId(1L);
    }

    @Test
    void findMedicalServiceByClinicIdThrowsNotFoundExceptionWhenClinicNotFound() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            medicalServiceService.findMedicalServiceByClinicId(1L);
        });

        verify(clinicRepository, times(1)).findById(1L);
        verify(medicalServiceRepository, never()).findByClinicId(anyLong());
    }


    @Test
    void findAllMedicalServiceSuccess() {
        when(medicalServiceRepository.findAll()).thenReturn(List.of(medicalService));

        List<MedicalServiceDto> result = medicalServiceService.findAllMedicalService();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Тестовая услуга", result.get(0).getTitle());

        verify(medicalServiceRepository, times(1)).findAll();
    }

    @Test
    void findAllMedicalServiceReturnsEmptyListWhenNoServices() {
        when(medicalServiceRepository.findAll()).thenReturn(List.of());

        List<MedicalServiceDto> result = medicalServiceService.findAllMedicalService();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(medicalServiceRepository, times(1)).findAll();
    }

    @Test
    void findMedicalServiceByIdSuccess() {
        when(medicalServiceRepository.findById(1L)).thenReturn(Optional.of(medicalService));

        MedicalServiceDto result = medicalServiceService.findMedicalServiceById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Тестовая услуга", result.getTitle());

        verify(medicalServiceRepository, times(1)).findById(1L);
    }

    @Test
    void findMedicalServiceByIdThrowsNotFoundExceptionWhenServiceNotFound() {
        when(medicalServiceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            medicalServiceService.findMedicalServiceById(1L);
        });

        verify(medicalServiceRepository, times(1)).findById(1L);
    }
}
