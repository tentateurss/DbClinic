package ru.tentateursss.patient.controller.adminapi;

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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.tentateursss.patient.dto.NewPatientDto;
import ru.tentateursss.patient.dto.PatientDto;
import ru.tentateursss.patient.service.PatientService;
import ru.tentateursss.exception.Error;

@Slf4j
@RestController
@RequestMapping("/admin/patients")
@RequiredArgsConstructor
@Validated
@Tag(name = "Admin: Управление пациентами", description = "Создание, редактирование и удаление пациентов")
public class AdminPatientController {

    private final PatientService patientService;

    @Operation(
            summary = "Зарегистрировать нового пациента",
            description = "Добавляет пациента в указанную клинику. Проверяет уникальность телефона и email. " +
                    "Дата рождения должна быть в прошлом"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пациент успешно зарегистрирован",
                    content = @Content(schema = @Schema(implementation = PatientDto.class))),
            @ApiResponse(responseCode = "409", description = "Пациент с таким телефоном или email уже существует",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "Клиника не найдена",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации полей",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto createPatient(
            @Parameter(description = "Данные нового пациента", required = true)
            @Valid @RequestBody NewPatientDto dto) {
        log.info("Admin API: Создание пациента: {}", dto);
        return patientService.createPatient(dto);
    }

    @Operation(
            summary = "Обновить данные пациента",
            description = "Полностью обновляет информацию о пациенте. Если изменились телефон или email, проверяется их уникальность"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные пациента успешно обновлены",
                    content = @Content(schema = @Schema(implementation = PatientDto.class))),
            @ApiResponse(responseCode = "404", description = "Пациент или клиника не найдены",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "409", description = "Новый телефон или email уже заняты другим пациентом",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации полей",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PutMapping("/{patientId}")
    public PatientDto updatePatient(
            @Parameter(description = "ID пациента", required = true, example = "1")
            @PathVariable Long patientId,
            @Parameter(description = "Новые данные пациента", required = true)
            @Valid @RequestBody NewPatientDto dto) {
        log.info("Admin API: Обновление пациента: {}", patientId);
        return patientService.updatePatient(patientId, dto);
    }

    @Operation(
            summary = "Удалить пациента",
            description = "Удаляет пациента и все его записи на приём из системы"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пациент успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Пациент не найден",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @DeleteMapping("/{patientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(
            @Parameter(description = "ID пациента для удаления", required = true, example = "1")
            @PathVariable Long patientId) {
        log.info("Admin API: Удаление пациента: {}", patientId);
        patientService.deletePatient(patientId);
    }
}