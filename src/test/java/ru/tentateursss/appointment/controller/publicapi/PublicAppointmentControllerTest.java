package ru.tentateursss.appointment.controller.publicapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import static org.mockito.ArgumentMatchers.any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.tentateursss.appointment.dto.AppointmentDto;
import ru.tentateursss.appointment.service.AppointmentService;
import ru.tentateursss.enums.AppointmentStatus;
import ru.tentateursss.exception.ErrorHandler;
import ru.tentateursss.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicAppointmentController.class)
@Import(ErrorHandler.class)
@ActiveProfiles("test")
class PublicAppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    private AppointmentDto appointmentDto;

    @BeforeEach
    void setUp() {
        appointmentDto = new AppointmentDto();
        appointmentDto.setId(1L);
        appointmentDto.setDateTime(LocalDateTime.now().plusDays(1));
        appointmentDto.setStatus(AppointmentStatus.SCHEDULED);
        appointmentDto.setIsPaid(false);
        appointmentDto.setNotes("Тестовая запись");
    }

    @Test
    void getAppointmentByIdSuccess() throws Exception {
        when(appointmentService.getAppointmentById(1L)).thenReturn(appointmentDto);

        mockMvc.perform(get("/public/appointments/{appId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("SCHEDULED")));

        verify(appointmentService, times(1)).getAppointmentById(1L);
    }

    @Test
    void getAppointmentByIdThrowsNotFound() throws Exception {
        when(appointmentService.getAppointmentById(999L))
                .thenThrow(new NotFoundException("Запись с ID 999 не найдена"));

        mockMvc.perform(get("/public/appointments/{appId}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllAppointmentsSuccess() throws Exception {
        Page<AppointmentDto> page = new PageImpl<>(List.of(appointmentDto));

        when(appointmentService.getAllAppointments(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/public/appointments")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)));

        verify(appointmentService, times(1)).getAllAppointments(any(Pageable.class));
    }

    @Test
    void getAllAppointmentsReturnsEmptyList() throws Exception {
        Page<AppointmentDto> emptyPage = new PageImpl<>(List.of());

        when(appointmentService.getAllAppointments(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/public/appointments")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        verify(appointmentService, times(1)).getAllAppointments(any(Pageable.class));
    }

    @Test
    void getAppointmentsByPatientIdSuccess() throws Exception {
        when(appointmentService.getAppointmentsByPatientId(1L)).thenReturn(List.of(appointmentDto));

        mockMvc.perform(get("/public/appointments/patient/{patientId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(appointmentService, times(1)).getAppointmentsByPatientId(1L);
    }

    @Test
    void getAppointmentsByPatientIdReturnsEmptyList() throws Exception {
        when(appointmentService.getAppointmentsByPatientId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/public/appointments/patient/{patientId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(appointmentService, times(1)).getAppointmentsByPatientId(1L);
    }

    @Test
    void getAppointmentsByEmployeeIdSuccess() throws Exception {
        when(appointmentService.getAppointmentsByEmployeeId(1L)).thenReturn(List.of(appointmentDto));

        mockMvc.perform(get("/public/appointments/employee/{employeeId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(appointmentService, times(1)).getAppointmentsByEmployeeId(1L);
    }

    @Test
    void getAppointmentsByClinicIdSuccess() throws Exception {
        when(appointmentService.getAppointmentsByClinicId(1L)).thenReturn(List.of(appointmentDto));

        mockMvc.perform(get("/public/appointments/clinic/{clinicId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(appointmentService, times(1)).getAppointmentsByClinicId(1L);
    }

    @Test
    void getAppointmentsByMedicalServiceIdSuccess() throws Exception {
        when(appointmentService.getAppointmentsByMedicalServiceId(1L)).thenReturn(List.of(appointmentDto));

        mockMvc.perform(get("/public/appointments/service/{serviceId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(appointmentService, times(1)).getAppointmentsByMedicalServiceId(1L);
    }

    @Test
    void getAppointmentsByStatusSuccess() throws Exception {
        when(appointmentService.getAppointmentsByStatus(AppointmentStatus.SCHEDULED))
                .thenReturn(List.of(appointmentDto));

        mockMvc.perform(get("/public/appointments/status")
                        .param("status", "SCHEDULED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("SCHEDULED")));

        verify(appointmentService, times(1)).getAppointmentsByStatus(AppointmentStatus.SCHEDULED);
    }

    @Test
    void getAppointmentsByPatientAndStatusSuccess() throws Exception {
        when(appointmentService.getAppointmentsByPatientIdAndStatus(1L, AppointmentStatus.SCHEDULED))
                .thenReturn(List.of(appointmentDto));

        mockMvc.perform(get("/public/appointments/patient/{patientId}/status", 1L)
                        .param("status", "SCHEDULED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(appointmentService, times(1))
                .getAppointmentsByPatientIdAndStatus(1L, AppointmentStatus.SCHEDULED);
    }

    @Test
    void getAppointmentsByEmployeeAndStatusSuccess() throws Exception {
        when(appointmentService.getAppointmentsByEmployeeIdAndStatus(1L, AppointmentStatus.SCHEDULED))
                .thenReturn(List.of(appointmentDto));

        mockMvc.perform(get("/public/appointments/employee/{employeeId}/status", 1L)
                        .param("status", "SCHEDULED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(appointmentService, times(1))
                .getAppointmentsByEmployeeIdAndStatus(1L, AppointmentStatus.SCHEDULED);
    }

    @Test
    void getAppointmentsByDateRangeSuccess() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        when(appointmentService.getAppointmentsByDateRange(start, end))
                .thenReturn(List.of(appointmentDto));

        mockMvc.perform(get("/public/appointments/date-range")
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(appointmentService, times(1)).getAppointmentsByDateRange(start, end);
    }

    @Test
    void getAppointmentsByEmployeeAndDateRangeSuccess() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        when(appointmentService.getAppointmentsByEmployeeAndDateRange(1L, start, end))
                .thenReturn(List.of(appointmentDto));

        mockMvc.perform(get("/public/appointments/employee/{employeeId}/date-range", 1L)
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(appointmentService, times(1))
                .getAppointmentsByEmployeeAndDateRange(1L, start, end);
    }
}