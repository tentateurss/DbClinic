package ru.tentateursss.medicalservice.controller.adminapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.tentateursss.exception.ErrorHandler;
import ru.tentateursss.exception.NotFoundException;
import ru.tentateursss.medicalservice.dto.MedicalServiceDto;
import ru.tentateursss.medicalservice.dto.NewMedicalServiceDto;
import ru.tentateursss.medicalservice.service.MedicalServiceService;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminMedicalServiceController.class)
@Import(ErrorHandler.class)
@ActiveProfiles("test")
class AdminMedicalServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicalServiceService service;

    @Autowired
    private ObjectMapper objectMapper;

    private MedicalServiceDto medicalServiceDto;
    private NewMedicalServiceDto newMedicalServiceDto;

    @BeforeEach
    void setUp() {
        medicalServiceDto = new MedicalServiceDto();
        medicalServiceDto.setId(1L);
        medicalServiceDto.setTitle("Тестовая услуга");
        medicalServiceDto.setDescription("Описание тестовой услуги");
        medicalServiceDto.setCost(1000);
        medicalServiceDto.setDurationMinutes(30);

        newMedicalServiceDto = new NewMedicalServiceDto();
        newMedicalServiceDto.setTitle("Тестовая услуга");
        newMedicalServiceDto.setDescription("Описание тестовой услуги");
        newMedicalServiceDto.setCost(1000);
        newMedicalServiceDto.setDurationMinutes(30);
        newMedicalServiceDto.setClinicId(1L);
    }

    @Test
    void createMedicalServiceSuccess() throws Exception {
        when(service.createMedicalService(any(NewMedicalServiceDto.class))).thenReturn(medicalServiceDto);

        mockMvc.perform(post("/admin/ms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicalServiceDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Тестовая услуга")))
                .andExpect(jsonPath("$.cost", is(1000)));

        verify(service, times(1)).createMedicalService(any(NewMedicalServiceDto.class));
    }

    @Test
    void createMedicalServiceThrowsValidationError() throws Exception {
        NewMedicalServiceDto invalidDto = new NewMedicalServiceDto();
        invalidDto.setTitle("");

        mockMvc.perform(post("/admin/ms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateMedicalServiceSuccess() throws Exception {
        when(service.updateMedicalService(eq(1L), any(NewMedicalServiceDto.class))).thenReturn(medicalServiceDto);

        mockMvc.perform(put("/admin/ms/{msId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicalServiceDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Тестовая услуга")));

        verify(service, times(1)).updateMedicalService(eq(1L), any(NewMedicalServiceDto.class));
    }

    @Test
    void updateMedicalServiceThrowsNotFound() throws Exception {
        when(service.updateMedicalService(eq(999L), any(NewMedicalServiceDto.class)))
                .thenThrow(new NotFoundException("Услуга с ID 999 не найдена"));

        mockMvc.perform(put("/admin/ms/{msId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicalServiceDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMedicalServiceSuccess() throws Exception {
        doNothing().when(service).deleteMedicalService(1L);

        mockMvc.perform(delete("/admin/ms/{msId}", 1L))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteMedicalService(1L);
    }

    @Test
    void deleteMedicalServiceThrowsNotFound() throws Exception {
        doThrow(new NotFoundException("Услуга с ID 999 не найдена")).when(service).deleteMedicalService(999L);

        mockMvc.perform(delete("/admin/ms/{msId}", 999L))
                .andExpect(status().isNotFound());

        verify(service, times(1)).deleteMedicalService(999L);
    }
}