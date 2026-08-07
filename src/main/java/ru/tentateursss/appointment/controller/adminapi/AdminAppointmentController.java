package ru.tentateursss.appointment.controller.adminapi;

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
import ru.tentateursss.appointment.dto.AppointmentDto;
import ru.tentateursss.appointment.dto.NewAppointmentDto;
import ru.tentateursss.appointment.service.AppointmentService;
import ru.tentateursss.exception.Error;

@Slf4j
@RestController
@RequestMapping("/admin/appointments")
@RequiredArgsConstructor
@Tag(name = "Admin: Управление записями", description = "Создание, редактирование и управление статусами записей на прием")
public class AdminAppointmentController {

    private final AppointmentService appointmentService;

    @Operation(
            summary = "Создать новую запись на прием",
            description = "Создаёт запись для пациента к врачу в указанное время. " +
                    "Проверяет доступность времени у врача с учетом длительности услуги."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Запись успешно создана",
                    content = @Content(schema = @Schema(implementation = AppointmentDto.class))),
            @ApiResponse(responseCode = "404", description = "Пациент/врач/клиника/услуга не найдены",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "409", description = "Время занято другим приёмом",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации полей",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentDto createAppointment(
            @Parameter(description = "Данные новой записи", required = true)
            @Valid @RequestBody NewAppointmentDto dto) {
        log.info("Admin API: создание записи: {}", dto);
        return appointmentService.createAppointment(dto);
    }

    @Operation(
            summary = "Обновить запись на прием",
            description = "Полностью обновляет данные существующей записи. Можно изменить время, врача, услугу."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запись успешно обновлена"),
            @ApiResponse(responseCode = "404", description = "Запись не найдена"),
            @ApiResponse(responseCode = "409", description = "Новое время занято")
    })
    @PutMapping("/{appId}")
    public AppointmentDto updateAppointment(
            @Parameter(description = "ID записи", required = true, example = "1")
            @PathVariable Long appId,
            @Valid @RequestBody NewAppointmentDto dto) {
        log.info("Admin API: обновление записи с ID: {}", appId);
        return appointmentService.updateAppointment(appId, dto);
    }

    @Operation(summary = "Удалить запись", description = "Полностью удаляет запись из системы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Запись удалена"),
            @ApiResponse(responseCode = "404", description = "Запись не найдена")
    })
    @DeleteMapping("/{appId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAppointment(
            @Parameter(description = "ID записи", required = true, example = "1")
            @PathVariable Long appId) {
        log.info("Admin API: удаление записи с ID: {}", appId);
        appointmentService.deleteAppointment(appId);
    }

    @Operation(summary = "Подтвердить запись", description = "Меняет статус с SCHEDULED на CONFIRMED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запись подтверждена"),
            @ApiResponse(responseCode = "404", description = "Запись не найдена"),
            @ApiResponse(responseCode = "409", description = "Недопустимый переход статуса")
    })
    @PatchMapping("/{appId}/confirm")
    public AppointmentDto confirmAppointment(
            @Parameter(description = "ID записи", required = true, example = "1")
            @PathVariable Long appId) {
        log.info("Admin API: подтверждение записи с ID: {}", appId);
        return appointmentService.confirmAppointment(appId);
    }

    @Operation(summary = "Отменить запись", description = "Меняет статус на CANCELLED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запись отменена"),
            @ApiResponse(responseCode = "404", description = "Запись не найдена"),
            @ApiResponse(responseCode = "409", description = "Недопустимый переход статуса (завершенные записи нельзя отменить)")
    })
    @PatchMapping("/{appId}/cancel")
    public AppointmentDto cancelAppointment(
            @Parameter(description = "ID записи", required = true, example = "1")
            @PathVariable Long appId) {
        log.info("Admin API: отмена записи с ID: {}", appId);
        return appointmentService.cancelAppointment(appId);
    }

    @Operation(summary = "Завершить прием", description = "Меняет статус на COMPLETED. Означает, что пациент был на приеме.")
    @PatchMapping("/{appId}/complete")
    public AppointmentDto completeAppointment(
            @Parameter(description = "ID записи", required = true, example = "1")
            @PathVariable Long appId) {
        log.info("Admin API: завершение записи с ID: {}", appId);
        return appointmentService.completeAppointment(appId);
    }

    @Operation(summary = "Отметить неявку", description = "Меняет статус на NO_SHOW. Означает, что пациент не пришел.")
    @PatchMapping("/{appId}/no-show")
    public AppointmentDto markAsNoShow(
            @Parameter(description = "ID записи", required = true, example = "1")
            @PathVariable Long appId) {
        log.info("Admin API: отметка о неявке для записи с ID: {}", appId);
        return appointmentService.markAsNoShow(appId);
    }
}