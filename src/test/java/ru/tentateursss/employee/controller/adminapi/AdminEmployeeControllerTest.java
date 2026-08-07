package ru.tentateursss.employee.controller.adminapi;

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
import ru.tentateursss.employee.dto.EmployeeDto;
import ru.tentateursss.employee.dto.NewEmployeeDto;
import ru.tentateursss.employee.service.EmployeeService;
import ru.tentateursss.enums.EmployeeRole;
import ru.tentateursss.exception.ConflictException;
import ru.tentateursss.exception.ErrorHandler;
import ru.tentateursss.exception.NotFoundException;

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

@WebMvcTest(AdminEmployeeController.class)
@Import(ErrorHandler.class)
@ActiveProfiles("test")
class AdminEmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeDto employeeDto;
    private NewEmployeeDto newEmployeeDto;

    @BeforeEach
    void setUp() {
        employeeDto = new EmployeeDto();
        employeeDto.setId(1L);
        employeeDto.setFullName("Петров Петр Петрович");
        employeeDto.setPhone("+79001234567");
        employeeDto.setEmail("petrov@mail.ru");
        employeeDto.setHireDate(LocalDate.now().minusDays(1)); // ← вчера
        employeeDto.setRole(EmployeeRole.DOCTOR);
        employeeDto.setSpecialization("Терапевт");
        employeeDto.setLicenseNumber("LIC-001");

        newEmployeeDto = new NewEmployeeDto();
        newEmployeeDto.setFullName("Петров Петр Петрович");
        newEmployeeDto.setPhone("+79001234567");
        newEmployeeDto.setEmail("petrov@mail.ru");
        newEmployeeDto.setHireDate(LocalDate.now().minusDays(1)); // ← вчера
        newEmployeeDto.setRole(EmployeeRole.DOCTOR);
        newEmployeeDto.setClinicId(1L);
        newEmployeeDto.setSpecialization("Терапевт");
        newEmployeeDto.setLicenseNumber("LIC-001");
    }

    @Test
    void createEmployeeSuccess() throws Exception {
        when(employeeService.createEmployee(any(NewEmployeeDto.class))).thenReturn(employeeDto);

        mockMvc.perform(post("/admin/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployeeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.fullName", is("Петров Петр Петрович")))
                .andExpect(jsonPath("$.role", is("DOCTOR")));

        verify(employeeService, times(1)).createEmployee(any(NewEmployeeDto.class));
    }

    @Test
    void createEmployeeThrowsConflictWhenEmailExists() throws Exception {
        when(employeeService.createEmployee(any(NewEmployeeDto.class)))
                .thenThrow(new ConflictException("Работник с почтой petrov@mail.ru уже существует"));

        mockMvc.perform(post("/admin/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployeeDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void createEmployeeThrowsConflictWhenPhoneExists() throws Exception {
        when(employeeService.createEmployee(any(NewEmployeeDto.class)))
                .thenThrow(new ConflictException("Работник с телефоном +79001234567 уже существует"));

        mockMvc.perform(post("/admin/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployeeDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void createEmployeeThrowsValidationError() throws Exception {
        NewEmployeeDto invalidDto = new NewEmployeeDto();
        invalidDto.setFullName("");

        mockMvc.perform(post("/admin/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEmployeeSuccess() throws Exception {
        when(employeeService.updateEmployee(eq(1L), any(NewEmployeeDto.class))).thenReturn(employeeDto);

        mockMvc.perform(put("/admin/employees/{employeeId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployeeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.fullName", is("Петров Петр Петрович")));

        verify(employeeService, times(1)).updateEmployee(eq(1L), any(NewEmployeeDto.class));
    }

    @Test
    void updateEmployeeThrowsNotFound() throws Exception {
        when(employeeService.updateEmployee(eq(999L), any(NewEmployeeDto.class)))
                .thenThrow(new NotFoundException("Работник с ID 999 не найден"));

        mockMvc.perform(put("/admin/employees/{employeeId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployeeDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateEmployeeThrowsConflictWhenEmailExists() throws Exception {
        when(employeeService.updateEmployee(eq(1L), any(NewEmployeeDto.class)))
                .thenThrow(new ConflictException("Работник с email petrov@mail.ru уже существует"));

        mockMvc.perform(put("/admin/employees/{employeeId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployeeDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteEmployeeSuccess() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/admin/employees/{employeeId}", 1L))
                .andExpect(status().isNoContent());

        verify(employeeService, times(1)).deleteEmployee(1L);
    }

    @Test
    void deleteEmployeeThrowsNotFound() throws Exception {
        doThrow(new NotFoundException("Работник с ID 999 не найден")).when(employeeService).deleteEmployee(999L);

        mockMvc.perform(delete("/admin/employees/{employeeId}", 999L))
                .andExpect(status().isNotFound());

        verify(employeeService, times(1)).deleteEmployee(999L);
    }
}