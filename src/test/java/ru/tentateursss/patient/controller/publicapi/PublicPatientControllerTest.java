package ru.tentateursss.patient.controller.publicapi;

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
import ru.tentateursss.exception.ErrorHandler;
import ru.tentateursss.exception.NotFoundException;
import ru.tentateursss.patient.dto.PatientDto;
import ru.tentateursss.patient.service.PatientService;

import java.time.LocalDate;
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

@WebMvcTest(PublicPatientController.class)
@Import(ErrorHandler.class)
@ActiveProfiles("test")
class PublicPatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    private PatientDto patientDto;

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
    }

    @Test
    void getPatientByIdSuccess() throws Exception {
        when(patientService.getPatient(1L)).thenReturn(patientDto);

        mockMvc.perform(get("/public/patients/{patientId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.fullName", is("Иванов Иван Иванович")))
                .andExpect(jsonPath("$.phone", is("+79001234567")));

        verify(patientService, times(1)).getPatient(1L);
    }

    @Test
    void getPatientByIdThrowsNotFound() throws Exception {
        when(patientService.getPatient(999L)).thenThrow(new NotFoundException("Пациент с ID 999 не найден"));

        mockMvc.perform(get("/public/patients/{patientId}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPatientsSuccess() throws Exception {
        Page<PatientDto> page = new PageImpl<>(List.of(patientDto));

        when(patientService.getAllPatients(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/public/patients")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].fullName", is("Иванов Иван Иванович")));

        verify(patientService, times(1)).getAllPatients(any(Pageable.class));
    }

    @Test
    void getAllPatientsReturnsEmptyList() throws Exception {
        Page<PatientDto> emptyPage = new PageImpl<>(List.of());

        when(patientService.getAllPatients(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/public/patients")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        verify(patientService, times(1)).getAllPatients(any(Pageable.class));
    }

    @Test
    void getPatientsByClinicIdSuccess() throws Exception {
        when(patientService.getAllPatientsByClinicId(1L)).thenReturn(List.of(patientDto));

        mockMvc.perform(get("/public/patients/clinic/{clinicId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].fullName", is("Иванов Иван Иванович")));

        verify(patientService, times(1)).getAllPatientsByClinicId(1L);
    }

    @Test
    void getPatientsByClinicIdReturnsEmptyList() throws Exception {
        when(patientService.getAllPatientsByClinicId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/public/patients/clinic/{clinicId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(patientService, times(1)).getAllPatientsByClinicId(1L);
    }

    @Test
    void getPatientsByClinicIdThrowsNotFound() throws Exception {
        when(patientService.getAllPatientsByClinicId(999L))
                .thenThrow(new NotFoundException("Клиника с ID 999 не найдена"));

        mockMvc.perform(get("/public/patients/clinic/{clinicId}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPatientByEmailSuccess() throws Exception {
        when(patientService.getPatientByEmail("ivan@mail.ru")).thenReturn(patientDto);

        mockMvc.perform(get("/public/patients/email")
                        .param("email", "ivan@mail.ru"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("ivan@mail.ru")));

        verify(patientService, times(1)).getPatientByEmail("ivan@mail.ru");
    }

    @Test
    void getPatientByEmailThrowsNotFound() throws Exception {
        when(patientService.getPatientByEmail("unknown@mail.ru"))
                .thenThrow(new NotFoundException("Пациент с email unknown@mail.ru не найден"));

        mockMvc.perform(get("/public/patients/email")
                        .param("email", "unknown@mail.ru"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPatientByPhoneSuccess() throws Exception {
        when(patientService.getPatientByPhone("+79001234567")).thenReturn(patientDto);

        mockMvc.perform(get("/public/patients/phone")
                        .param("phone", "+79001234567"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.phone", is("+79001234567")));

        verify(patientService, times(1)).getPatientByPhone("+79001234567");
    }

    @Test
    void getPatientByPhoneThrowsNotFound() throws Exception {
        when(patientService.getPatientByPhone("+79999999999"))
                .thenThrow(new NotFoundException("Пациент с телефоном +79999999999 не найден"));

        mockMvc.perform(get("/public/patients/phone")
                        .param("phone", "+79999999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPatientByFullNameSuccess() throws Exception {
        when(patientService.getPatientByFullName("Иванов")).thenReturn(List.of(patientDto));

        mockMvc.perform(get("/public/patients/search")
                        .param("fullName", "Иванов"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].fullName", is("Иванов Иван Иванович")));

        verify(patientService, times(1)).getPatientByFullName("Иванов");
    }

    @Test
    void getPatientByFullNameReturnsEmptyList() throws Exception {
        when(patientService.getPatientByFullName("Неизвестный")).thenReturn(List.of());

        mockMvc.perform(get("/public/patients/search")
                        .param("fullName", "Неизвестный"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(patientService, times(1)).getPatientByFullName("Неизвестный");
    }
}