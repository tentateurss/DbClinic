package ru.tentateursss.appointment.controller.publicapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.tentateursss.appointment.dto.AppointmentDto;
import ru.tentateursss.appointment.service.AppointmentService;
import ru.tentateursss.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/public/appointments")
@RequiredArgsConstructor
@Tag(name = "Public: Записи на приём", description = "Получение информации о записях: поиск по пациенту, врачу, клинике, статусу и дате")
public class PublicAppointmentController {

    private final AppointmentService appointmentService;

    @Operation(summary = "Получить запись по ID", description = "Возвращает полную информацию о записи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запись найдена"),
            @ApiResponse(responseCode = "404", description = "Запись не найдена")
    })
    @GetMapping("/{appId}")
    public AppointmentDto getAppointmentById(
            @Parameter(description = "ID записи", required = true, example = "1")
            @PathVariable Long appId) {
        log.info("Public API: получение записи с ID: {}", appId);
        return appointmentService.getAppointmentById(appId);
    }

    @Operation(summary = "Получить все записи с пагинацией и сортировкой")
    @GetMapping
    public Page<AppointmentDto> getAllAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateTime,asc") String sort) {
        log.info("Public API: получение всех записей (страница {}, размер {}, сортировка {})", page, size, sort);
        return appointmentService.getAllAppointments(parsePageRequest(page, size, sort));
    }

    @Operation(summary = "Записи конкретного пациента")
    @GetMapping("/patient/{patientId}")
    public Page<AppointmentDto> getAppointmentsByPatientId(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateTime,asc") String sort) {
        log.info("Public API: получение записей пациента с ID: {}", patientId);
        return appointmentService.getAppointmentsByPatientId(patientId, parsePageRequest(page, size, sort));
    }

    @Operation(summary = "Записи конкретного врача")
    @GetMapping("/employee/{employeeId}")
    public Page<AppointmentDto> getAppointmentsByEmployeeId(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateTime,asc") String sort) {
        log.info("Public API: получение записей сотрудника с ID: {}", employeeId);
        return appointmentService.getAppointmentsByEmployeeId(employeeId, parsePageRequest(page, size, sort));
    }

    @Operation(summary = "Записи в конкретной клинике")
    @GetMapping("/clinic/{clinicId}")
    public Page<AppointmentDto> getAppointmentsByClinicId(
            @PathVariable Long clinicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateTime,asc") String sort) {
        log.info("Public API: получение записей клиники с ID: {}", clinicId);
        return appointmentService.getAppointmentsByClinicId(clinicId, parsePageRequest(page, size, sort));
    }

    @Operation(summary = "Записи по медицинской услуге")
    @GetMapping("/service/{serviceId}")
    public Page<AppointmentDto> getAppointmentsByMedicalServiceId(
            @PathVariable Long serviceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateTime,asc") String sort) {
        log.info("Public API: получение записей по услуге с ID: {}", serviceId);
        return appointmentService.getAppointmentsByMedicalServiceId(serviceId, parsePageRequest(page, size, sort));
    }

    @Operation(summary = "Записи по статусу")
    @GetMapping("/status")
    public Page<AppointmentDto> getAppointmentsByStatus(
            @RequestParam AppointmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateTime,asc") String sort) {
        log.info("Public API: получение записей со статусом: {}", status);
        return appointmentService.getAppointmentsByStatus(status, parsePageRequest(page, size, sort));
    }

    @Operation(summary = "Записи пациента по статусу")
    @GetMapping("/patient/{patientId}/status")
    public Page<AppointmentDto> getAppointmentsByPatientAndStatus(
            @PathVariable Long patientId,
            @RequestParam AppointmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateTime,asc") String sort) {
        log.info("Public API: получение записей пациента {} со статусом: {}", patientId, status);
        return appointmentService.getAppointmentsByPatientIdAndStatus(patientId, status, parsePageRequest(page, size, sort));
    }

    @Operation(summary = "Записи врача по статусу")
    @GetMapping("/employee/{employeeId}/status")
    public Page<AppointmentDto> getAppointmentsByEmployeeAndStatus(
            @PathVariable Long employeeId,
            @RequestParam AppointmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateTime,asc") String sort) {
        log.info("Public API: получение записей сотрудника {} со статусом: {}", employeeId, status);
        return appointmentService.getAppointmentsByEmployeeIdAndStatus(employeeId, status, parsePageRequest(page, size, sort));
    }

    @Operation(summary = "Записи за период")
    @GetMapping("/date-range")
    public Page<AppointmentDto> getAppointmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateTime,asc") String sort) {
        log.info("Public API: получение записей за период с {} по {}", start, end);
        return appointmentService.getAppointmentsByDateRange(start, end, parsePageRequest(page, size, sort));
    }

    @Operation(summary = "Записи врача за период")
    @GetMapping("/employee/{employeeId}/date-range")
    public Page<AppointmentDto> getAppointmentsByEmployeeAndDateRange(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dateTime,asc") String sort) {
        log.info("Public API: получение записей сотрудника {} за период с {} по {}", employeeId, start, end);
        return appointmentService.getAppointmentsByEmployeeAndDateRange(employeeId, start, end, parsePageRequest(page, size, sort));
    }

    @Operation(
            summary = "Записи по нескольким статусам",
            description = "Возвращает записи с любым из указанных статусов, с пагинацией и сортировкой. " +
                    "Статусы передаются через запятую: SCHEDULED,CONFIRMED,COMPLETED,CANCELLED,NO_SHOW"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Страница с записями"),
            @ApiResponse(responseCode = "400", description = "Неверный формат статуса")
    })
    @GetMapping("/statuses")
    public Page<AppointmentDto> getAppointmentsByStatuses(
            @Parameter(description = "Список статусов через запятую", required = true, example = "SCHEDULED,CONFIRMED")
            @RequestParam List<AppointmentStatus> statuses,
            @Parameter(description = "Номер страницы (начиная с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Количество записей на странице", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Сортировка в формате поле,направление", example = "dateTime,asc")
            @RequestParam(defaultValue = "dateTime,asc") String sort) {
        log.info("Public API: получение записей со статусами: {} (страница {}, размер {}, сортировка {})",
                statuses, page, size, sort);
        return appointmentService.getAppointmentsByStatuses(statuses, parsePageRequest(page, size, sort));
    }

    private PageRequest parsePageRequest(int page, int size, String sort) {
        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = Sort.Direction.fromString(parts[1]);
        return PageRequest.of(page, size, Sort.by(direction, field));
    }
}