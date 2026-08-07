package ru.tentateursss.clinic.service;

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
import ru.tentateursss.clinic.dto.ClinicDto;
import ru.tentateursss.clinic.dto.NewClinicDto;
import ru.tentateursss.clinic.mapper.ClinicMapper;
import ru.tentateursss.clinic.model.Clinic;
import ru.tentateursss.clinic.repository.ClinicRepository;
import ru.tentateursss.exception.ConflictException;
import ru.tentateursss.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicServiceImplTest {

    @Mock
    private ClinicRepository clinicRepository;

    @InjectMocks
    private ClinicServiceImpl clinicService;

    private Clinic clinic;
    private NewClinicDto newClinicDto;

    @BeforeEach
    void setUp() {
        clinic = Clinic.builder()
                .id(1L)
                .clinicCode("ЦК-1")
                .name("Центральная клиника")
                .address("ул. Ленина, 1")
                .phone("+78008008080")
                .email("info@clinic.ru")
                .inn("123456789012")
                .build();

        newClinicDto = new NewClinicDto();
        newClinicDto.setName("Центральная клиника");
        newClinicDto.setAddress("ул. Ленина, 1");
        newClinicDto.setPhone("+78008008080");
        newClinicDto.setEmail("info@clinic.ru");
        newClinicDto.setInn("123456789012");
    }

    @Test
    void createClinicSuccess() {
        when(clinicRepository.existsByInn(anyString())).thenReturn(false);
        when(clinicRepository.save(any(Clinic.class))).thenReturn(clinic);

        ClinicDto result = clinicService.createClinic(newClinicDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Центральная клиника", result.getName());
        assertEquals("123456789012", result.getInn());

        verify(clinicRepository, times(1)).existsByInn(anyString());
        verify(clinicRepository, times(2)).save(any(Clinic.class));
    }

    @Test
    void createClinicThrowsConflictExceptionWhenInnExists() {
        when(clinicRepository.existsByInn(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () -> {
            clinicService.createClinic(newClinicDto);
        });

        verify(clinicRepository, times(1)).existsByInn(anyString());
        verify(clinicRepository, never()).save(any(Clinic.class));
    }

    @Test
    void getClinicSuccess() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));

        ClinicDto result = clinicService.getClinic(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Центральная клиника", result.getName());

        verify(clinicRepository, times(1)).findById(1L);
    }

    @Test
    void getClinicThrowsNotFoundExceptionWhenNotExists() {
        when(clinicRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            clinicService.getClinic(999L);
        });

        verify(clinicRepository, times(1)).findById(999L);
    }

    @Test
    void updateClinicSuccess() {
        NewClinicDto updateDto = new NewClinicDto();
        updateDto.setName("Обновленная клиника");
        updateDto.setAddress("ул. Новая, 10");
        updateDto.setPhone("+78008008081");
        updateDto.setEmail("new@clinic.ru");
        updateDto.setInn("123456789012");

        Clinic updatedClinic = Clinic.builder()
                .id(1L)
                .clinicCode("ЦК-1")
                .name("Обновленная клиника")
                .address("ул. Новая, 10")
                .phone("+78008008081")
                .email("new@clinic.ru")
                .inn("123456789012")
                .build();

        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
        when(clinicRepository.save(any(Clinic.class))).thenReturn(updatedClinic);

        ClinicDto result = clinicService.updateClinic(1L, updateDto);

        assertNotNull(result);
        assertEquals("Обновленная клиника", result.getName());
        assertEquals("ул. Новая, 10", result.getAddress());

        verify(clinicRepository, times(1)).findById(1L);
        verify(clinicRepository, times(1)).save(any(Clinic.class));
    }

    @Test
    void deleteClinic_Success() {
        when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));

        clinicService.deleteClinic(1L);

        verify(clinicRepository, times(1)).findById(1L);
        verify(clinicRepository, times(1)).delete(clinic);
    }

    @Test
    void deleteClinic_ThrowsNotFoundException_WhenNotExists() {
        when(clinicRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            clinicService.deleteClinic(999L);
        });

        verify(clinicRepository, times(1)).findById(999L);
        verify(clinicRepository, never()).delete(any(Clinic.class));
    }

    @Test
    void getAllClinics_Success() {
        Page<Clinic> page = new PageImpl<>(List.of(clinic));

        when(clinicRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<ClinicDto> result = clinicService.getAllClinics(PageRequest.of(0, 20));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Центральная клиника", result.getContent().get(0).getName());

        verify(clinicRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllClinicsEmptyList() {
        Page<Clinic> emptyPage = new PageImpl<>(List.of());

        when(clinicRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        Page<ClinicDto> result = clinicService.getAllClinics(PageRequest.of(0, 20));

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());

        verify(clinicRepository, times(1)).findAll(any(Pageable.class));
    }
}