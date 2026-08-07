package ru.tentateursss.appointment.controller.adminapi;

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
import ru.tentateursss.appointment.dto.AppointmentDto;
import ru.tentateursss.appointment.dto.NewAppointmentDto;
import ru.tentateursss.appointment.service.AppointmentService;
import ru.tentateursss.enums.AppointmentStatus;
import ru.tentateursss.exception.DateTimeConflict;
import ru.tentateursss.exception.ErrorHandler;
import ru.tentateursss.exception.NotFoundException;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminAppointmentController.class)
@Import(ErrorHandler.class)
@ActiveProfiles("test")
class AdminAppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private AppointmentDto appointmentDto;
    private NewAppointmentDto newAppointmentDto;

    @BeforeEach
    void setUp() {
        appointmentDto = new AppointmentDto();
        appointmentDto.setId(1L);
        appointmentDto.setDateTime(LocalDateTime.now().plusDays(1));
        appointmentDto.setStatus(AppointmentStatus.SCHEDULED);
        appointmentDto.setIsPaid(false);
        appointmentDto.setNotes("Тестовая запись");

        newAppointmentDto = new NewAppointmentDto();
        newAppointmentDto.setPatientId(1L);
        newAppointmentDto.setEmployeeId(1L);
        newAppointmentDto.setClinicId(1L);
        newAppointmentDto.setMedicalServiceId(1L);
        newAppointmentDto.setDateTime(LocalDateTime.now().plusDays(1));
        newAppointmentDto.setIsPaid(false);
        newAppointmentDto.setNotes("Тестовая запись");
    }

    @Test
    void createAppointmentSuccess() throws Exception {
        when(appointmentService.createAppointment(any(NewAppointmentDto.class))).thenReturn(appointmentDto);

        mockMvc.perform(post("/admin/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAppointmentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("SCHEDULED")));

        verify(appointmentService, times(1)).createAppointment(any(NewAppointmentDto.class));
    }

    @Test
    void createAppointmentThrowsDateTimeConflict() throws Exception {
        when(appointmentService.createAppointment(any(NewAppointmentDto.class)))
                .thenThrow(new DateTimeConflict("Время занято"));

        mockMvc.perform(post("/admin/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAppointmentDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void createAppointmentThrowsNotFound() throws Exception {
        when(appointmentService.createAppointment(any(NewAppointmentDto.class)))
                .thenThrow(new NotFoundException("Пациент не найден"));

        mockMvc.perform(post("/admin/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAppointmentDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAppointmentThrowsValidationError() throws Exception {
        NewAppointmentDto invalidDto = new NewAppointmentDto();

        mockMvc.perform(post("/admin/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateAppointmentSuccess() throws Exception {
        when(appointmentService.updateAppointment(eq(1L), any(NewAppointmentDto.class))).thenReturn(appointmentDto);

        mockMvc.perform(put("/admin/appointments/{appId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAppointmentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(appointmentService, times(1)).updateAppointment(eq(1L), any(NewAppointmentDto.class));
    }

    @Test
    void updateAppointmentThrowsNotFound() throws Exception {
        when(appointmentService.updateAppointment(eq(999L), any(NewAppointmentDto.class)))
                .thenThrow(new NotFoundException("Запись с ID 999 не найдена"));

        mockMvc.perform(put("/admin/appointments/{appId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAppointmentDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAppointmentThrowsDateTimeConflict() throws Exception {
        when(appointmentService.updateAppointment(eq(1L), any(NewAppointmentDto.class)))
                .thenThrow(new DateTimeConflict("Время занято"));

        mockMvc.perform(put("/admin/appointments/{appId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAppointmentDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteAppointmentSuccess() throws Exception {
        doNothing().when(appointmentService).deleteAppointment(1L);

        mockMvc.perform(delete("/admin/appointments/{appId}", 1L))
                .andExpect(status().isNoContent());

        verify(appointmentService, times(1)).deleteAppointment(1L);
    }

    @Test
    void deleteAppointmentThrowsNotFound() throws Exception {
        doThrow(new NotFoundException("Запись с ID 999 не найдена")).when(appointmentService).deleteAppointment(999L);

        mockMvc.perform(delete("/admin/appointments/{appId}", 999L))
                .andExpect(status().isNotFound());

        verify(appointmentService, times(1)).deleteAppointment(999L);
    }

    @Test
    void confirmAppointmentSuccess() throws Exception {
        AppointmentDto confirmedDto = appointmentDto;
        confirmedDto.setStatus(AppointmentStatus.CONFIRMED);

        when(appointmentService.confirmAppointment(1L)).thenReturn(confirmedDto);

        mockMvc.perform(patch("/admin/appointments/{appId}/confirm", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("CONFIRMED")));

        verify(appointmentService, times(1)).confirmAppointment(1L);
    }

    @Test
    void confirmAppointmentThrowsNotFound() throws Exception {
        when(appointmentService.confirmAppointment(999L))
                .thenThrow(new NotFoundException("Запись с ID 999 не найдена"));

        mockMvc.perform(patch("/admin/appointments/{appId}/confirm", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelAppointmentSuccess() throws Exception {
        AppointmentDto cancelledDto = appointmentDto;
        cancelledDto.setStatus(AppointmentStatus.CANCELLED);

        when(appointmentService.cancelAppointment(1L)).thenReturn(cancelledDto);

        mockMvc.perform(patch("/admin/appointments/{appId}/cancel", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("CANCELLED")));

        verify(appointmentService, times(1)).cancelAppointment(1L);
    }

    @Test
    void completeAppointmentSuccess() throws Exception {
        AppointmentDto completedDto = appointmentDto;
        completedDto.setStatus(AppointmentStatus.COMPLETED);

        when(appointmentService.completeAppointment(1L)).thenReturn(completedDto);

        mockMvc.perform(patch("/admin/appointments/{appId}/complete", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("COMPLETED")));

        verify(appointmentService, times(1)).completeAppointment(1L);
    }

    @Test
    void markAsNoShowSuccess() throws Exception {
        AppointmentDto noShowDto = appointmentDto;
        noShowDto.setStatus(AppointmentStatus.NO_SHOW);

        when(appointmentService.markAsNoShow(1L)).thenReturn(noShowDto);

        mockMvc.perform(patch("/admin/appointments/{appId}/no-show", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("NO_SHOW")));

        verify(appointmentService, times(1)).markAsNoShow(1L);
    }
}