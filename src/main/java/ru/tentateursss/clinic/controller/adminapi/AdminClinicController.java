package ru.tentateursss.clinic.controller.adminapi;

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
import ru.tentateursss.clinic.dto.ClinicDto;
import ru.tentateursss.clinic.dto.NewClinicDto;
import ru.tentateursss.clinic.service.ClinicService;
import ru.tentateursss.exception.Error;

@Slf4j
@RestController
@RequestMapping("/admin/clinics")
@RequiredArgsConstructor
@Validated
@Tag(name = "Admin: Управление клиниками", description = "Создание, редактирование и удаление клиник")
public class AdminClinicController {

    private final ClinicService clinicService;

    @Operation(
            summary = "Создать новую клинику",
            description = "Создаёт клинику с автоматической генерацией уникального кода. ИНН должен быть уникальным"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Клиника успешно создана",
                    content = @Content(schema = @Schema(implementation = ClinicDto.class))),
            @ApiResponse(responseCode = "409", description = "Клиника с таким ИНН уже существует",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации полей (телефон, email, ИНН)",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClinicDto createClinic(
            @Parameter(description = "Данные новой клиники", required = true)
            @Valid @RequestBody NewClinicDto clinicDto) {
        log.info("Admin API: Создание клиники: {}", clinicDto);
        return clinicService.createClinic(clinicDto);
    }

    @Operation(
            summary = "Обновить данные клиники",
            description = "Полностью обновляет информацию о клинике. Если меняется ИНН, проверяется его уникальность"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Клиника успешно обновлена",
                    content = @Content(schema = @Schema(implementation = ClinicDto.class))),
            @ApiResponse(responseCode = "404", description = "Клиника с указанным ID не найдена",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "409", description = "Новый ИНН уже занят другой клиникой",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации полей",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PutMapping("/{clinicId}")
    public ClinicDto updateClinic(
            @Parameter(description = "ID клиники", required = true, example = "1")
            @PathVariable Long clinicId,
            @Parameter(description = "Новые данные клиники", required = true)
            @Valid @RequestBody NewClinicDto clinicDto) {
        log.info("Admin API: Обновление клиники: {}", clinicId);
        return clinicService.updateClinic(clinicId, clinicDto);
    }

    @Operation(
            summary = "Удалить клинику",
            description = "Полностью удаляет клинику и все связанные данные: сотрудников, пациентов, записи, услуги"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Клиника успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Клиника с указанным ID не найдена",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @DeleteMapping("/{clinicId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClinic(
            @Parameter(description = "ID клиники для удаления", required = true, example = "1")
            @PathVariable Long clinicId) {
        log.info("Admin API: Удаление клиники: {}", clinicId);
        clinicService.deleteClinic(clinicId);
    }
}