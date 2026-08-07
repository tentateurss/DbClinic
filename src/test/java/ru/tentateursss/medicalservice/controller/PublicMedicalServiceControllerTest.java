package ru.tentateursss.medicalservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.tentateursss.exception.ErrorHandler;
import ru.tentateursss.exception.NotFoundException;
import ru.tentateursss.medicalservice.controller.publicapi.PublicMedicalServiceController;
import ru.tentateursss.medicalservice.dto.MedicalServiceDto;
import ru.tentateursss.medicalservice.service.MedicalServiceService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicMedicalServiceController.class)
@Import(ErrorHandler.class)
@ActiveProfiles("test")
class PublicMedicalServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicalServiceService service;

    private MedicalServiceDto medicalServiceDto;

    @BeforeEach
    void setUp() {
        medicalServiceDto = new MedicalServiceDto();
        medicalServiceDto.setId(1L);
        medicalServiceDto.setTitle("Тестовая услуга");
        medicalServiceDto.setDescription("Описание тестовой услуги");
        medicalServiceDto.setCost(1000);
        medicalServiceDto.setDurationMinutes(30);
    }

    @Test
    void getAllMedicalServicesSuccess() throws Exception {
        when(service.findAllMedicalService()).thenReturn(List.of(medicalServiceDto));

        mockMvc.perform(get("/public/ms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Тестовая услуга")));

        verify(service, times(1)).findAllMedicalService();
    }

    @Test
    void getAllMedicalServicesReturnsEmptyList() throws Exception {
        when(service.findAllMedicalService()).thenReturn(List.of());

        mockMvc.perform(get("/public/ms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(service, times(1)).findAllMedicalService();
    }

    @Test
    void getMedicalServiceByIdSuccess() throws Exception {
        when(service.findMedicalServiceById(1L)).thenReturn(medicalServiceDto);

        mockMvc.perform(get("/public/ms/{msId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Тестовая услуга")))
                .andExpect(jsonPath("$.cost", is(1000)));

        verify(service, times(1)).findMedicalServiceById(1L);
    }

    @Test
    void getMedicalServiceByIdThrowsNotFound() throws Exception {
        when(service.findMedicalServiceById(999L))
                .thenThrow(new NotFoundException("Услуга с ID 999 не найдена"));

        mockMvc.perform(get("/public/ms/{msId}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMedicalServicesByClinicIdSuccess() throws Exception {
        when(service.findMedicalServiceByClinicId(1L)).thenReturn(List.of(medicalServiceDto));

        mockMvc.perform(get("/public/ms/clinic/{clinicId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Тестовая услуга")));

        verify(service, times(1)).findMedicalServiceByClinicId(1L);
    }

    @Test
    void getMedicalServicesByClinicIdReturnsEmptyList() throws Exception {
        when(service.findMedicalServiceByClinicId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/public/ms/clinic/{clinicId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(service, times(1)).findMedicalServiceByClinicId(1L);
    }

    @Test
    void getMedicalServicesByClinicIdThrowsNotFound() throws Exception {
        when(service.findMedicalServiceByClinicId(999L))
                .thenThrow(new NotFoundException("Клиника с ID 999 не найдена"));

        mockMvc.perform(get("/public/ms/clinic/{clinicId}", 999L))
                .andExpect(status().isNotFound());
    }
}