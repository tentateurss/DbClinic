package ru.tentateursss.clinic.controller.adminapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.tentateursss.clinic.dto.ClinicDto;
import ru.tentateursss.clinic.dto.NewClinicDto;
import ru.tentateursss.clinic.service.ClinicService;
import ru.tentateursss.exception.ConflictException;
import ru.tentateursss.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminClinicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClinicService clinicService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClinicDto clinicDto;
    private NewClinicDto newClinicDto;

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

        newClinicDto = new NewClinicDto();
        newClinicDto.setName("Центральная клиника");
        newClinicDto.setAddress("ул. Ленина, 1");
        newClinicDto.setPhone("+78008008080");
        newClinicDto.setEmail("info@clinic.ru");
        newClinicDto.setInn("123456789012");
    }

    @Test
    void createClinicSuccess() throws Exception {
        when(clinicService.createClinic(any(NewClinicDto.class))).thenReturn(clinicDto);

        mockMvc.perform(post("/admin/clinics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClinicDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Центральная клиника")))
                .andExpect(jsonPath("$.clinicCode", is("ЦК-1")));

        verify(clinicService, times(1)).createClinic(any(NewClinicDto.class));
    }

    @Test
    void createClinicThrowsConflictWhenInnExists() throws Exception {
        when(clinicService.createClinic(any(NewClinicDto.class)))
                .thenThrow(new ConflictException("Клиника с ИНН 123456789012 уже существует"));

        mockMvc.perform(post("/admin/clinics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClinicDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void createClinicThrowsValidationErrorWhenNameEmpty() throws Exception {
        NewClinicDto invalidDto = new NewClinicDto();
        invalidDto.setName("");

        mockMvc.perform(post("/admin/clinics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateClinicSuccess() throws Exception {
        when(clinicService.updateClinic(eq(1L), any(NewClinicDto.class))).thenReturn(clinicDto);

        mockMvc.perform(put("/admin/clinics/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClinicDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Центральная клиника")));

        verify(clinicService, times(1)).updateClinic(eq(1L), any(NewClinicDto.class));
    }

    @Test
    void updateClinicThrowsNotFound() throws Exception {
        when(clinicService.updateClinic(eq(999L), any(NewClinicDto.class)))
                .thenThrow(new NotFoundException("Клиника с ID 999 не найдена"));

        mockMvc.perform(put("/admin/clinics/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClinicDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateClinicThrowsConflictWhenInnExists() throws Exception {
        when(clinicService.updateClinic(eq(1L), any(NewClinicDto.class)))
                .thenThrow(new ConflictException("Клиника с ИНН 123456789012 уже существует"));

        mockMvc.perform(put("/admin/clinics/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newClinicDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteClinicSuccess() throws Exception {
        doNothing().when(clinicService).deleteClinic(1L);

        mockMvc.perform(delete("/admin/clinics/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(clinicService, times(1)).deleteClinic(1L);
    }

    @Test
    void deleteClinicThrowsNotFound() throws Exception {
        doThrow(new NotFoundException("Клиника с ID 999 не найдена")).when(clinicService).deleteClinic(999L);

        mockMvc.perform(delete("/admin/clinics/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(clinicService, times(1)).deleteClinic(999L);
    }
}