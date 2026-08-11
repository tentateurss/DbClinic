package ru.tentateursss.stats.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.tentateursss.enums.AppointmentStatus;
import ru.tentateursss.enums.EmployeeRole;
import ru.tentateursss.exception.NotFoundException;
import ru.tentateursss.stats.dto.ClinicSummaryDto;
import ru.tentateursss.stats.dto.DoctorStatsDto;
import ru.tentateursss.stats.service.StatsService;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminStatsController.class)
@ActiveProfiles("test")
class AdminStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatsService statsService;

    private ClinicSummaryDto summaryDto;

    @BeforeEach
    void setUp() {
        summaryDto = ClinicSummaryDto.builder()
                .totalEmployees(5).totalDoctors(3).totalPatients(50)
                .totalAppointments(100).scheduledAppointments(10)
                .confirmedAppointments(20).completedAppointments(50)
                .cancelledAppointments(15).noShowAppointments(5)
                .build();
    }

    @Test
    void getStatisticsByRoleAndClinicIdSuccess() throws Exception {
        when(statsService.getEmployeeCountByRole(1L))
                .thenReturn(Map.of(EmployeeRole.DOCTOR, 2L, EmployeeRole.ADMIN, 1L));

        mockMvc.perform(get("/admin/stats/clinics/role/{clinicId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.DOCTOR", is(2)))
                .andExpect(jsonPath("$.ADMIN", is(1)));

        verify(statsService, times(1)).getEmployeeCountByRole(1L);
    }

    @Test
    void getStatisticsByRoleAndClinicIdThrowsNotFound() throws Exception {
        when(statsService.getEmployeeCountByRole(999L))
                .thenThrow(new NotFoundException("Клиника с ID 999 не найдена"));

        mockMvc.perform(get("/admin/stats/clinics/role/{clinicId}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getStatisticsByStatusAndClinicIdSuccess() throws Exception {
        when(statsService.getAppointmentCountByStatus(1L))
                .thenReturn(Map.of(AppointmentStatus.SCHEDULED, 5L, AppointmentStatus.COMPLETED, 3L));

        mockMvc.perform(get("/admin/stats/clinics/appStatus/{clinicId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.SCHEDULED", is(5)))
                .andExpect(jsonPath("$.COMPLETED", is(3)));

        verify(statsService, times(1)).getAppointmentCountByStatus(1L);
    }

    @Test
    void getStatisticsByStatusAndClinicIdThrowsNotFound() throws Exception {
        when(statsService.getAppointmentCountByStatus(999L))
                .thenThrow(new NotFoundException("Клиника с ID 999 не найдена"));

        mockMvc.perform(get("/admin/stats/clinics/appStatus/{clinicId}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDoctorStatsSuccess() throws Exception {
        DoctorStatsDto doctor = new DoctorStatsDto(1L, "Петров Петр Петрович", 10L);
        when(statsService.getDoctorStats(1L)).thenReturn(List.of(doctor));

        mockMvc.perform(get("/admin/stats/clinics/doctors/{clinicId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].doctorId", is(1)))
                .andExpect(jsonPath("$[0].fullName", is("Петров Петр Петрович")))
                .andExpect(jsonPath("$[0].totalAppointments", is(10)));

        verify(statsService, times(1)).getDoctorStats(1L);
    }

    @Test
    void getDoctorStatsThrowsNotFound() throws Exception {
        when(statsService.getDoctorStats(999L))
                .thenThrow(new NotFoundException("Клиника с ID 999 не найдена"));

        mockMvc.perform(get("/admin/stats/clinics/doctors/{clinicId}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPatientCountByClinicIdSuccess() throws Exception {
        when(statsService.getPatientCountByClinicId(1L)).thenReturn(10L);

        mockMvc.perform(get("/admin/stats/clinics/patients/{clinicId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(10)));

        verify(statsService, times(1)).getPatientCountByClinicId(1L);
    }

    @Test
    void getPatientCountByClinicIdThrowsNotFound() throws Exception {
        when(statsService.getPatientCountByClinicId(999L))
                .thenThrow(new NotFoundException("Клиника с ID 999 не найдена"));

        mockMvc.perform(get("/admin/stats/clinics/patients/{clinicId}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getClinicSummarySuccess() throws Exception {
        when(statsService.getClinicSummary(1L)).thenReturn(summaryDto);

        mockMvc.perform(get("/admin/stats/summary/{clinicId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmployees", is(5)))
                .andExpect(jsonPath("$.totalDoctors", is(3)))
                .andExpect(jsonPath("$.totalPatients", is(50)))
                .andExpect(jsonPath("$.totalAppointments", is(100)))
                .andExpect(jsonPath("$.scheduledAppointments", is(10)))
                .andExpect(jsonPath("$.confirmedAppointments", is(20)))
                .andExpect(jsonPath("$.completedAppointments", is(50)))
                .andExpect(jsonPath("$.cancelledAppointments", is(15)))
                .andExpect(jsonPath("$.noShowAppointments", is(5)));

        verify(statsService, times(1)).getClinicSummary(1L);
    }

    @Test
    void getClinicSummaryThrowsNotFound() throws Exception {
        when(statsService.getClinicSummary(999L))
                .thenThrow(new NotFoundException("Клиника с ID 999 не найдена"));

        mockMvc.perform(get("/admin/stats/summary/{clinicId}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOverallSummarySuccess() throws Exception {
        when(statsService.getOverallSummary()).thenReturn(summaryDto);

        mockMvc.perform(get("/admin/stats/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmployees", is(5)))
                .andExpect(jsonPath("$.totalDoctors", is(3)))
                .andExpect(jsonPath("$.totalAppointments", is(100)));

        verify(statsService, times(1)).getOverallSummary();
    }
}