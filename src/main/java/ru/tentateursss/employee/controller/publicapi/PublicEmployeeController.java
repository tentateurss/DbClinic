package ru.tentateursss.employee.controller.publicapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.tentateursss.employee.dto.EmployeeDto;
import ru.tentateursss.employee.service.EmployeeService;
import ru.tentateursss.enums.EmployeeRole;
import ru.tentateursss.exception.Error;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/public/employees")
@RequiredArgsConstructor
@Tag(name = "Public: Сотрудники", description = "Получение информации о сотрудниках клиник: поиск по клинике, роли, ID")
public class PublicEmployeeController {

    private final EmployeeService employeeService;

    @Operation(
            summary = "Получить сотрудника по ID",
            description = "Возвращает полную информацию о сотруднике: ФИО, контакты, роль, специализацию, клинику"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сотрудник найден",
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "404", description = "Сотрудник не найден",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/{employeeId}")
    public EmployeeDto getEmployeeById(
            @Parameter(description = "ID сотрудника", required = true, example = "1")
            @PathVariable Long employeeId) {
        log.info("Public API: получение работника с ID: {}", employeeId);
        return employeeService.getEmployeeById(employeeId);
    }

    @Operation(
            summary = "Получить всех сотрудников с пагинацией",
            description = "Возвращает список всех сотрудников всех клиник постранично. По умолчанию 20 сотрудников на странице, начиная с первой"
    )
    @ApiResponse(responseCode = "200", description = "Страница со списком сотрудников")
    @GetMapping
    public Page<EmployeeDto> getAllEmployees(
            @Parameter(description = "Номер страницы (начиная с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Количество сотрудников на странице", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        log.info("Public API: получение всех работников (страница {}, размер {})", page, size);
        return employeeService.getAllEmployees(PageRequest.of(page, size));
    }

    @Operation(
            summary = "Сотрудники конкретной клиники",
            description = "Возвращает всех сотрудников указанной клиники, независимо от роли"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список сотрудников клиники"),
            @ApiResponse(responseCode = "404", description = "Клиника не найдена",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/clinic/{clinicId}")
    public List<EmployeeDto> getEmployeesByClinicId(
            @Parameter(description = "ID клиники", required = true, example = "1")
            @PathVariable Long clinicId) {
        log.info("Public API: получение работников клиники с ID: {}", clinicId);
        return employeeService.getEmployeesByClinicId(clinicId);
    }

    @Operation(
            summary = "Сотрудники по роли",
            description = "Фильтр сотрудников по роли: DOCTOR, ADMIN, MANAGER, ACCOUNTANT, NURSE. " +
                    "Например, чтобы получить всех врачей, укажите DOCTOR"
    )
    @ApiResponse(responseCode = "200", description = "Список сотрудников с указанной ролью")
    @GetMapping("/role/{role}")
    public List<EmployeeDto> getEmployeesByRole(
            @Parameter(description = "Роль сотрудника", required = true, example = "DOCTOR")
            @PathVariable EmployeeRole role) {
        log.info("Public API: получение работников с ролью: {}", role);
        return employeeService.getEmployeesByRole(role);
    }

    @Operation(
            summary = "Сотрудники клиники по роли",
            description = "Комбинированный фильтр: все сотрудники указанной клиники с конкретной ролью. " +
                    "Например, все врачи (DOCTOR) в клинике 1, или все администраторы (ADMIN) в клинике 2"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отфильтрованный список сотрудников"),
            @ApiResponse(responseCode = "404", description = "Клиника не найдена",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/clinic/{clinicId}/role/{role}")
    public List<EmployeeDto> getEmployeesByClinicAndRole(
            @Parameter(description = "ID клиники", required = true, example = "1")
            @PathVariable Long clinicId,
            @Parameter(description = "Роль сотрудника", required = true, example = "DOCTOR")
            @PathVariable EmployeeRole role) {
        log.info("Public API: получение работников клиники {} с ролью: {}", clinicId, role);
        return employeeService.getEmployeesByClinicIdAndRole(clinicId, role);
    }
}