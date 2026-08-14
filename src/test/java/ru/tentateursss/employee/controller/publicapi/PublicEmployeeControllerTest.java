package ru.tentateursss.employee.controller.publicapi;

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
import ru.tentateursss.employee.dto.EmployeeDto;
import ru.tentateursss.employee.service.EmployeeService;
import ru.tentateursss.enums.EmployeeRole;
import ru.tentateursss.exception.ErrorHandler;
import ru.tentateursss.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicEmployeeController.class)
@Import(ErrorHandler.class)
@ActiveProfiles("test")
class PublicEmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    private EmployeeDto employeeDto;

    @BeforeEach
    void setUp() {
        employeeDto = new EmployeeDto();
        employeeDto.setId(1L);
        employeeDto.setFullName("Петров Петр Петрович");
        employeeDto.setPhone("+79001234567");
        employeeDto.setEmail("petrov@mail.ru");
        employeeDto.setHireDate(LocalDate.now());
        employeeDto.setRole(EmployeeRole.DOCTOR);
        employeeDto.setSpecialization("Терапевт");
        employeeDto.setLicenseNumber("LIC-001");
    }

    @Test
    void getEmployeeByIdSuccess() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(employeeDto);

        mockMvc.perform(get("/public/employees/{employeeId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.fullName", is("Петров Петр Петрович")))
                .andExpect(jsonPath("$.role", is("DOCTOR")));

        verify(employeeService, times(1)).getEmployeeById(1L);
    }

    @Test
    void getEmployeeByIdThrowsNotFound() throws Exception {
        when(employeeService.getEmployeeById(999L))
                .thenThrow(new NotFoundException("Работник с ID 999 не найден"));

        mockMvc.perform(get("/public/employees/{employeeId}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllEmployeesSuccess() throws Exception {
        Page<EmployeeDto> page = new PageImpl<>(List.of(employeeDto));

        when(employeeService.getAllEmployees(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/public/employees")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].fullName", is("Петров Петр Петрович")));

        verify(employeeService, times(1)).getAllEmployees(any(Pageable.class));
    }

    @Test
    void getAllEmployeesReturnsEmptyList() throws Exception {
        Page<EmployeeDto> emptyPage = new PageImpl<>(List.of());

        when(employeeService.getAllEmployees(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/public/employees")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        verify(employeeService, times(1)).getAllEmployees(any(Pageable.class));
    }

    @Test
    void getEmployeesByClinicIdSuccess() throws Exception {
        Page<EmployeeDto> page = new PageImpl<>(List.of(employeeDto));

        when(employeeService.getEmployeesByClinicId(eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/public/employees/clinic/{clinicId}", 1L)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].fullName", is("Петров Петр Петрович")));

        verify(employeeService, times(1)).getEmployeesByClinicId(eq(1L), any(Pageable.class));
    }

    @Test
    void getEmployeesByClinicIdReturnsEmptyList() throws Exception {
        Page<EmployeeDto> emptyPage = new PageImpl<>(List.of());

        when(employeeService.getEmployeesByClinicId(eq(1L), any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/public/employees/clinic/{clinicId}", 1L)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        verify(employeeService, times(1)).getEmployeesByClinicId(eq(1L), any(Pageable.class));
    }

    @Test
    void getEmployeesByClinicIdThrowsNotFound() throws Exception {
        when(employeeService.getEmployeesByClinicId(eq(999L), any(Pageable.class)))
                .thenThrow(new NotFoundException("Клиника с ID 999 не найдена"));

        mockMvc.perform(get("/public/employees/clinic/{clinicId}", 999L)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getEmployeesByRoleSuccess() throws Exception {
        Page<EmployeeDto> page = new PageImpl<>(List.of(employeeDto));

        when(employeeService.getEmployeesByRole(eq(EmployeeRole.DOCTOR), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/public/employees/role/{role}", EmployeeRole.DOCTOR)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].role", is("DOCTOR")));

        verify(employeeService, times(1)).getEmployeesByRole(eq(EmployeeRole.DOCTOR), any(Pageable.class));
    }

    @Test
    void getEmployeesByRoleReturnsEmptyList() throws Exception {
        Page<EmployeeDto> emptyPage = new PageImpl<>(List.of());

        when(employeeService.getEmployeesByRole(eq(EmployeeRole.ADMIN), any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/public/employees/role/{role}", EmployeeRole.ADMIN)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        verify(employeeService, times(1)).getEmployeesByRole(eq(EmployeeRole.ADMIN), any(Pageable.class));
    }

    @Test
    void getEmployeesByClinicAndRoleSuccess() throws Exception {
        Page<EmployeeDto> page = new PageImpl<>(List.of(employeeDto));

        when(employeeService.getEmployeesByClinicIdAndRole(eq(1L), eq(EmployeeRole.DOCTOR), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/public/employees/clinic/{clinicId}/role/{role}", 1L, EmployeeRole.DOCTOR)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].role", is("DOCTOR")));

        verify(employeeService, times(1)).getEmployeesByClinicIdAndRole(eq(1L), eq(EmployeeRole.DOCTOR), any(Pageable.class));
    }

    @Test
    void getEmployeesByClinicAndRoleReturnsEmptyList() throws Exception {
        Page<EmployeeDto> emptyPage = new PageImpl<>(List.of());

        when(employeeService.getEmployeesByClinicIdAndRole(eq(1L), eq(EmployeeRole.ADMIN), any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/public/employees/clinic/{clinicId}/role/{role}", 1L, EmployeeRole.ADMIN)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        verify(employeeService, times(1)).getEmployeesByClinicIdAndRole(eq(1L), eq(EmployeeRole.ADMIN), any(Pageable.class));
    }

    @Test
    void getEmployeesByClinicAndRoleThrowsNotFound() throws Exception {
        when(employeeService.getEmployeesByClinicIdAndRole(eq(999L), eq(EmployeeRole.DOCTOR), any(Pageable.class)))
                .thenThrow(new NotFoundException("Клиника с ID 999 не найдена"));

        mockMvc.perform(get("/public/employees/clinic/{clinicId}/role/{role}", 999L, EmployeeRole.DOCTOR)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getEmployeesBySpecializationSuccess() throws Exception {
        Page<EmployeeDto> page = new PageImpl<>(List.of(employeeDto));

        when(employeeService.getEmployeesBySpecializationContainingIgnoreCase(eq("Терапевт"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/public/employees/specialization")
                        .param("specialization", "Терапевт")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].specialization", is("Терапевт")));

        verify(employeeService, times(1)).getEmployeesBySpecializationContainingIgnoreCase(eq("Терапевт"), any(Pageable.class));
    }

    @Test
    void getEmployeesBySpecializationReturnsEmptyList() throws Exception {
        Page<EmployeeDto> emptyPage = new PageImpl<>(List.of());

        when(employeeService.getEmployeesBySpecializationContainingIgnoreCase(eq("Хирург"), any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/public/employees/specialization")
                        .param("specialization", "Хирург")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));

        verify(employeeService, times(1)).getEmployeesBySpecializationContainingIgnoreCase(eq("Хирург"), any(Pageable.class));
    }
}