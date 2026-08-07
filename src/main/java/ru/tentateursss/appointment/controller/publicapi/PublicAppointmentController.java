package ru.tentateursss.appointment.controller.publicapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.tentateursss.appointment.dto.AppointmentDto;
import ru.tentateursss.appointment.service.AppointmentService;
import ru.tentateursss.enums.AppointmentStatus;
import ru.tentateursss.exception.Error;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/public/appointments")
@RequiredArgsConstructor
@Tag(name = "Public: Записи на приём", description = "Получение информации о записях: поиск по пациенту, врачу, клинике, статусу и дате")
public class PublicAppointmentController {

    private final AppointmentService appointmentService;

    @Operation(
            summary = "Получить запись по ID",
            description = "Возвращает полную информацию о записи, включая данные пациента, врача, клиники и услуги"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запись найдена",
                    content = @Content(schema = @Schema(implementation = AppointmentDto.class))),
            @ApiResponse(responseCode = "404", description = "Запись с указанным ID не найдена",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/{appId}")
    public AppointmentDto getAppointmentById(
            @Parameter(description = "ID записи", required = true, example = "1")
            @PathVariable Long appId) {
        log.info("Public API: получение записи с ID: {}", appId);
        return appointmentService.getAppointmentById(appId);
    }

    @Operation(
            summary = "Получить все записи",
            description = "Возвращает список всех записей во всех клиниках. Для больших объёмов данных рекомендуется использовать фильтры"
    )
    @ApiResponse(responseCode = "200", description = "Список всех записей (может быть пустым)")
    @GetMapping
    public List<AppointmentDto> getAllAppointments() {
        log.info("Public API: получение всех записей");
        return appointmentService.getAllAppointments();
    }

    @Operation(
            summary = "Записи конкретного пациента",
            description = "Возвращает все записи указанного пациента, отсортированные по дате"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список записей пациента (может быть пустым)"),
            @ApiResponse(responseCode = "404", description = "Пациент не найден")
    })
    @GetMapping("/patient/{patientId}")
    public List<AppointmentDto> getAppointmentsByPatientId(
            @Parameter(description = "ID пациента", required = true, example = "1")
            @PathVariable Long patientId) {
        log.info("Public API: получение записей пациента с ID: {}", patientId);
        return appointmentService.getAppointmentsByPatientId(patientId);
    }

    @Operation(
            summary = "Записи конкретного врача",
            description = "Возвращает все записи указанного сотрудника (врача), отсортированные по дате"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список записей врача (может быть пустым)"),
            @ApiResponse(responseCode = "404", description = "Сотрудник не найден")
    })
    @GetMapping("/employee/{employeeId}")
    public List<AppointmentDto> getAppointmentsByEmployeeId(
            @Parameter(description = "ID сотрудника (врача)", required = true, example = "1")
            @PathVariable Long employeeId) {
        log.info("Public API: получение записей сотрудника с ID: {}", employeeId);
        return appointmentService.getAppointmentsByEmployeeId(employeeId);
    }

    @Operation(
            summary = "Записи в конкретной клинике",
            description = "Возвращает все записи указанной клиники"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список записей клиники (может быть пустым)"),
            @ApiResponse(responseCode = "404", description = "Клиника не найдена")
    })
    @GetMapping("/clinic/{clinicId}")
    public List<AppointmentDto> getAppointmentsByClinicId(
            @Parameter(description = "ID клиники", required = true, example = "1")
            @PathVariable Long clinicId) {
        log.info("Public API: получение записей клиники с ID: {}", clinicId);
        return appointmentService.getAppointmentsByClinicId(clinicId);
    }

    @Operation(
            summary = "Записи по медицинской услуге",
            description = "Возвращает все записи, в которых указана конкретная медицинская услуга"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список записей по услуге (может быть пустым)"),
            @ApiResponse(responseCode = "404", description = "Услуга не найдена")
    })
    @GetMapping("/service/{serviceId}")
    public List<AppointmentDto> getAppointmentsByMedicalServiceId(
            @Parameter(description = "ID медицинской услуги", required = true, example = "1")
            @PathVariable Long serviceId) {
        log.info("Public API: получение записей по услуге с ID: {}", serviceId);
        return appointmentService.getAppointmentsByMedicalServiceId(serviceId);
    }

    @Operation(
            summary = "Записи по статусу",
            description = "Фильтр записей по статусу: SCHEDULED (запланирована), CONFIRMED (подтверждена), " +
                    "COMPLETED (завершена), CANCELLED (отменена), NO_SHOW (неявка)"
    )
    @ApiResponse(responseCode = "200", description = "Список записей с указанным статусом")
    @GetMapping("/status")
    public List<AppointmentDto> getAppointmentsByStatus(
            @Parameter(description = "Статус записи", required = true, example = "SCHEDULED")
            @RequestParam AppointmentStatus status) {
        log.info("Public API: получение записей со статусом: {}", status);
        return appointmentService.getAppointmentsByStatus(status);
    }

    @Operation(
            summary = "Записи пациента по статусу",
            description = "Комбинированный фильтр: все записи конкретного пациента с указанным статусом. " +
                    "Полезно для просмотра активных (SCHEDULED) или завершённых (COMPLETED) записей пациента"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отфильтрованный список записей"),
            @ApiResponse(responseCode = "404", description = "Пациент не найден")
    })
    @GetMapping("/patient/{patientId}/status")
    public List<AppointmentDto> getAppointmentsByPatientAndStatus(
            @Parameter(description = "ID пациента", required = true, example = "1")
            @PathVariable Long patientId,
            @Parameter(description = "Статус для фильтрации", required = true, example = "SCHEDULED")
            @RequestParam AppointmentStatus status) {
        log.info("Public API: получение записей пациента {} со статусом: {}", patientId, status);
        return appointmentService.getAppointmentsByPatientIdAndStatus(patientId, status);
    }

    @Operation(
            summary = "Записи врача по статусу",
            description = "Комбинированный фильтр: все записи конкретного врача с указанным статусом. " +
                    "Полезно для просмотра предстоящих приёмов (SCHEDULED, CONFIRMED) или истории (COMPLETED)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отфильтрованный список записей врача"),
            @ApiResponse(responseCode = "404", description = "Сотрудник не найден")
    })
    @GetMapping("/employee/{employeeId}/status")
    public List<AppointmentDto> getAppointmentsByEmployeeAndStatus(
            @Parameter(description = "ID сотрудника (врача)", required = true, example = "1")
            @PathVariable Long employeeId,
            @Parameter(description = "Статус для фильтрации", required = true, example = "CONFIRMED")
            @RequestParam AppointmentStatus status) {
        log.info("Public API: получение записей сотрудника {} со статусом: {}", employeeId, status);
        return appointmentService.getAppointmentsByEmployeeIdAndStatus(employeeId, status);
    }

    @Operation(
            summary = "Записи за период",
            description = "Возвращает все записи в указанном временном диапазоне. " +
                    "Формат даты: ISO 8601 (например, 2026-08-01T00:00:00)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список записей за период"),
            @ApiResponse(responseCode = "400", description = "Неверный формат даты")
    })
    @GetMapping("/date-range")
    public List<AppointmentDto> getAppointmentsByDateRange(
            @Parameter(description = "Начало периода (ISO 8601)", required = true, example = "2026-08-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "Конец периода (ISO 8601)", required = true, example = "2026-08-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("Public API: получение записей за период с {} по {}", start, end);
        return appointmentService.getAppointmentsByDateRange(start, end);
    }

    @Operation(
            summary = "Записи врача за период",
            description = "Комбинированный фильтр: все записи конкретного врача в указанном временном диапазоне. " +
                    "Удобно для построения расписания врача на день/неделю/месяц"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список записей врача за период"),
            @ApiResponse(responseCode = "404", description = "Сотрудник не найден")
    })
    @GetMapping("/employee/{employeeId}/date-range")
    public List<AppointmentDto> getAppointmentsByEmployeeAndDateRange(
            @Parameter(description = "ID сотрудника (врача)", required = true, example = "1")
            @PathVariable Long employeeId,
            @Parameter(description = "Начало периода (ISO 8601)", required = true, example = "2026-08-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "Конец периода (ISO 8601)", required = true, example = "2026-08-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("Public API: получение записей сотрудника {} за период с {} по {}", employeeId, start, end);
        return appointmentService.getAppointmentsByEmployeeAndDateRange(employeeId, start, end);
    }
}