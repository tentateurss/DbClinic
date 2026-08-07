package ru.tentateursss.patient.controller.adminapi;

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
import ru.tentateursss.exception.ConflictException;
import ru.tentateursss.exception.ErrorHandler;
import ru.tentateursss.exception.NotFoundException;
import ru.tentateursss.patient.dto.NewPatientDto;
import ru.tentateursss.patient.dto.PatientDto;
import ru.tentateursss.patient.service.PatientService;

import java.time.LocalDate;

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

@WebMvcTest(AdminPatientController.class)
@Import(ErrorHandler.class)
@ActiveProfiles("test")
class AdminPatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    private PatientDto patientDto;
    private NewPatientDto newPatientDto;

    @BeforeEach
    void setUp() {
        patientDto = new PatientDto();
        patientDto.setId(1L);
        patientDto.setFullName("Иванов Иван Иванович");
        patientDto.setPhone("+79001234567");
        patientDto.setEmail("ivan@mail.ru");
        patientDto.setBirthDate(LocalDate.of(1990, 1, 1));
        patientDto.setRegistrationDate(LocalDate.now());
        patientDto.setMedicalCardNumber("MC-001");
        patientDto.setNotes("Тестовый пациент");

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
    void createPatientSuccess() throws Exception {
        when(patientService.createPatient(any(NewPatientDto.class))).thenReturn(patientDto);

        mockMvc.perform(post("/admin/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatientDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.fullName", is("Иванов Иван Иванович")))
                .andExpect(jsonPath("$.phone", is("+79001234567")));

        verify(patientService, times(1)).createPatient(any(NewPatientDto.class));
    }

    @Test
    void createPatientThrowsConflictWhenPhoneExists() throws Exception {
        when(patientService.createPatient(any(NewPatientDto.class)))
                .thenThrow(new ConflictException("Пациент с телефоном +79001234567 уже существует"));

        mockMvc.perform(post("/admin/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatientDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void createPatientThrowsConflictWhenEmailExists() throws Exception {
        when(patientService.createPatient(any(NewPatientDto.class)))
                .thenThrow(new ConflictException("Пациент с email ivan@mail.ru уже существует"));

        mockMvc.perform(post("/admin/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatientDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void createPatientThrowsValidationError() throws Exception {
        NewPatientDto invalidDto = new NewPatientDto();
        invalidDto.setFullName("");

        mockMvc.perform(post("/admin/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePatientSuccess() throws Exception {
        when(patientService.updatePatient(eq(1L), any(NewPatientDto.class))).thenReturn(patientDto);

        mockMvc.perform(put("/admin/patients/{patientId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatientDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.fullName", is("Иванов Иван Иванович")));

        verify(patientService, times(1)).updatePatient(eq(1L), any(NewPatientDto.class));
    }

    @Test
    void updatePatientThrowsNotFound() throws Exception {
        when(patientService.updatePatient(eq(999L), any(NewPatientDto.class)))
                .thenThrow(new NotFoundException("Пациент с ID 999 не найден"));

        mockMvc.perform(put("/admin/patients/{patientId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatientDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePatientThrowsConflictWhenPhoneExists() throws Exception {
        when(patientService.updatePatient(eq(1L), any(NewPatientDto.class)))
                .thenThrow(new ConflictException("Пациент с телефоном +79001234567 уже существует"));

        mockMvc.perform(put("/admin/patients/{patientId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatientDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void deletePatientSuccess() throws Exception {
        doNothing().when(patientService).deletePatient(1L);

        mockMvc.perform(delete("/admin/patients/{patientId}", 1L))
                .andExpect(status().isNoContent());

        verify(patientService, times(1)).deletePatient(1L);
    }

    @Test
    void deletePatientThrowsNotFound() throws Exception {
        doThrow(new NotFoundException("Пациент с ID 999 не найден")).when(patientService).deletePatient(999L);

        mockMvc.perform(delete("/admin/patients/{patientId}", 999L))
                .andExpect(status().isNotFound());

        verify(patientService, times(1)).deletePatient(999L);
    }
}