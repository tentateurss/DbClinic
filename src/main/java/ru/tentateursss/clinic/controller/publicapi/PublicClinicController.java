package ru.tentateursss.clinic.controller.publicapi;

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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.tentateursss.clinic.dto.ClinicDto;
import ru.tentateursss.clinic.service.ClinicService;
import ru.tentateursss.exception.Error;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/public/clinics")
@RequiredArgsConstructor
@Validated
@Tag(name = "Public: Клиники", description = "Получение информации о клиниках")
public class PublicClinicController {

    private final ClinicService clinicService;

    @Operation(
            summary = "Получить клинику по ID",
            description = "Возвращает полную информацию о клинике: название, адрес, контакты, ИНН, код"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Клиника найдена",
                    content = @Content(schema = @Schema(implementation = ClinicDto.class))),
            @ApiResponse(responseCode = "404", description = "Клиника с указанным ID не найдена",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/{clinicId}")
    public ClinicDto getClinicById(
            @Parameter(description = "ID клиники", required = true, example = "1")
            @PathVariable Long clinicId) {
        log.info("Public API: Получение клиники: {}", clinicId);
        return clinicService.getClinic(clinicId);
    }

    @Operation(
            summary = "Получить список всех клиник с пагинацией",
            description = "Возвращает клиники постранично. По умолчанию 20 клиник на странице"
    )
    @ApiResponse(responseCode = "200", description = "Страница с клиниками")
    @GetMapping
    public Page<ClinicDto> getClinics(
            @Parameter(description = "Номер страницы (начиная с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Количество клиник на странице", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        log.info("Public API: Получение всех клиник (страница {}, размер {})", page, size);
        return clinicService.getAllClinics(PageRequest.of(page, size));
    }
}