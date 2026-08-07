package ru.tentateursss.clinic.controller.publicapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.tentateursss.clinic.dto.ClinicDto;
import ru.tentateursss.clinic.service.ClinicService;
import ru.tentateursss.exception.ErrorHandler;
import ru.tentateursss.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicClinicController.class)
@Import(ErrorHandler.class)
@ActiveProfiles("test")
class PublicClinicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClinicService clinicService;

    private ClinicDto clinicDto;

    @BeforeEach
    void setUp() {
        clinicDto = new ClinicDto();
        clinicDto.setId(1L);
        clinicDto.setClinicCode("ЦК-1");
        clinicDto.setName("Центральная клиника");
        clinicDto.setAddress("ул. Ленина, 1");
        clinicDto.setPhone("+78008008080");
        clinicDto.setEmail("info@clinic.ru");
        clinicDto.setInn("123456789012");
        clinicDto.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getClinicByIdSuccess() throws Exception {
        when(clinicService.getClinic(1L)).thenReturn(clinicDto);

        mockMvc.perform(get("/public/clinics/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Центральная клиника")))
                .andExpect(jsonPath("$.clinicCode", is("ЦК-1")));

        verify(clinicService, times(1)).getClinic(1L);
    }

    @Test
    void getClinicByIdThrowsNotFound() throws Exception {
        when(clinicService.getClinic(999L)).thenThrow(new NotFoundException("Клиника с ID 999 не найдена"));

        mockMvc.perform(get("/public/clinics/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllClinicsSuccess() throws Exception {
        Page<ClinicDto> page = new PageImpl<>(List.of(clinicDto));

        when(clinicService.getAllClinics(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/public/clinics")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("Центральная клиника")));

        verify(clinicService, times(1)).getAllClinics(any(Pageable.class));
    }

    @Test
    void getAllClinicsReturnsEmptyList() throws Exception {
        Page<ClinicDto> emptyPage = new PageImpl<>(List.of());

        when(clinicService.getAllClinics(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/public/clinics")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        verify(clinicService, times(1)).getAllClinics(any(Pageable.class));
    }
}