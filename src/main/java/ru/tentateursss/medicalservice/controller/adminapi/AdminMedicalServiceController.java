package ru.tentateursss.medicalservice.controller.adminapi;

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
import ru.tentateursss.medicalservice.dto.MedicalServiceDto;
import ru.tentateursss.medicalservice.dto.NewMedicalServiceDto;
import ru.tentateursss.medicalservice.service.MedicalServiceService;
import ru.tentateursss.exception.Error;

@Slf4j
@RestController
@RequestMapping("/admin/ms")
@RequiredArgsConstructor
@Validated
@Tag(name = "Admin: Управление услугами", description = "Создание, редактирование и удаление медицинских услуг")
public class AdminMedicalServiceController {

    private final MedicalServiceService service;

    @Operation(
            summary = "Создать новую медицинскую услугу",
            description = "Добавляет услугу в указанную клинику. Услуга содержит название, описание, стоимость и длительность в минутах"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Услуга успешно создана",
                    content = @Content(schema = @Schema(implementation = MedicalServiceDto.class))),
            @ApiResponse(responseCode = "404", description = "Клиника не найдена",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации полей (цена и длительность должны быть положительными)",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalServiceDto createMedicalService(
            @Parameter(description = "Данные новой услуги", required = true)
            @Valid @RequestBody NewMedicalServiceDto dto) {
        log.info("Admin API: Создание услуги: {}", dto.toString());
        return service.createMedicalService(dto);
    }

    @Operation(
            summary = "Обновить медицинскую услугу",
            description = "Полностью обновляет информацию об услуге: название, описание, стоимость, длительность, клинику"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Услуга успешно обновлена",
                    content = @Content(schema = @Schema(implementation = MedicalServiceDto.class))),
            @ApiResponse(responseCode = "404", description = "Услуга или клиника не найдены",
                    content = @Content(schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации полей",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @PutMapping("/{msId}")
    public MedicalServiceDto updateMedicalService(
            @Parameter(description = "ID услуги", required = true, example = "1")
            @PathVariable Long msId,
            @Parameter(description = "Новые данные услуги", required = true)
            @Valid @RequestBody NewMedicalServiceDto dto) {
        log.info("Admin API: Обновление услуги: {}", msId);
        return service.updateMedicalService(msId, dto);
    }

    @Operation(
            summary = "Удалить медицинскую услугу",
            description = "Удаляет услугу из системы. Записи на приём, связанные с этой услугой, останутся, но ссылка на услугу станет NULL"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Услуга успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Услуга не найдена",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @DeleteMapping("/{msId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMedicalService(
            @Parameter(description = "ID услуги для удаления", required = true, example = "1")
            @PathVariable Long msId) {
        log.info("Admin API: Удаление услуги: {}", msId);
        service.deleteMedicalService(msId);
    }
}