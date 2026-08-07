package ru.tentateursss.employee.controller.adminapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.tentateursss.employee.dto.EmployeeDto;
import ru.tentateursss.employee.dto.NewEmployeeDto;
import ru.tentateursss.employee.service.EmployeeService;
import ru.tentateursss.exception.Error;

@Slf4j
@RestController
@RequestMapping("/admin/employees")
@RequiredArgsConstructor
@Tag(name = "Admin: Управление сотрудниками", description = "Создание, редактирование и удаление сотрудников клиник")
public class AdminEmployeeController {

    private final EmployeeService employeeService;

    @Operation(
            summary = "Создать нового сотрудника",
            description = "Добавляет сотрудника в указанную клинику. Проверяет уникальность email, телефона и номера лицензии. " +
                    "Роли: DOCTOR (врач), ADMIN (администратор), MANAGER (управляющий), ACCOUNTANT (бухгалтер), NURSE (медсестра)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Сотрудник успешно создан",
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "409", description = "Email, телефон или номер лицензии уже заняты",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "Клиника не найдена",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации полей",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDto createEmployee(
            @Parameter(description = "Данные нового сотрудника", required = true)
            @Valid @RequestBody NewEmployeeDto dto) {
        log.info("Admin API: создание работника: {}", dto);
        return employeeService.createEmployee(dto);
    }

    @Operation(
            summary = "Обновить данные сотрудника",
            description = "Полностью обновляет информацию о сотруднике. Можно изменить роль, специализацию, клинику. " +
                    "Проверяет уникальность email, телефона и лицензии (если они изменились)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сотрудник успешно обновлён",
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "404", description = "Сотрудник или клиника не найдены",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "409", description = "Новый email/телефон/лицензия уже заняты",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации полей",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PutMapping("/{employeeId}")
    public EmployeeDto updateEmployee(
            @Parameter(description = "ID сотрудника", required = true, example = "1")
            @PathVariable Long employeeId,
            @Parameter(description = "Новые данные сотрудника", required = true)
            @Valid @RequestBody NewEmployeeDto dto) {
        log.info("Admin API: обновление работника с ID: {}", employeeId);
        return employeeService.updateEmployee(employeeId, dto);
    }

    @Operation(
            summary = "Удалить сотрудника",
            description = "Удаляет сотрудника из системы. Связанные записи на приём также будут удалены (CASCADE)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Сотрудник успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Сотрудник не найден",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @DeleteMapping("/{employeeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(
            @Parameter(description = "ID сотрудника для удаления", required = true, example = "1")
            @PathVariable Long employeeId) {
        log.info("Admin API: удаление работника с ID: {}", employeeId);
        employeeService.deleteEmployee(employeeId);
    }
}