package ru.tentateursss.medicalservice.controller.publicapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tentateursss.medicalservice.dto.MedicalServiceDto;
import ru.tentateursss.medicalservice.service.MedicalServiceService;
import ru.tentateursss.exception.Error;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/public/ms")
@RequiredArgsConstructor
@Validated
@Tag(name = "Public: Медицинские услуги", description = "Получение информации об услугах: поиск по клинике и ID")
public class PublicMedicalServiceController {

    private final MedicalServiceService service;

    @Operation(
            summary = "Услуги конкретной клиники",
            description = "Возвращает список всех медицинских услуг, доступных в указанной клинике, с ценами и длительностью"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список услуг клиники (может быть пустым)"),
            @ApiResponse(responseCode = "404", description = "Клиника не найдена",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/clinic/{clinicId}")
    public List<MedicalServiceDto> findMedicalServiceByClinicId(
            @Parameter(description = "ID клиники", required = true, example = "1")
            @PathVariable Long clinicId) {
        log.info("Public API: получение всех услуг в клинике с ID: {}", clinicId);
        return service.findMedicalServiceByClinicId(clinicId);
    }

    @Operation(
            summary = "Получить список всех услуг",
            description = "Возвращает все медицинские услуги во всех клиниках"
    )
    @ApiResponse(responseCode = "200", description = "Список всех услуг (может быть пустым)")
    @GetMapping
    public List<MedicalServiceDto> findAllMedicalService() {
        log.info("Public API: получение всех услуг");
        return service.findAllMedicalService();
    }

    @Operation(
            summary = "Получить услугу по ID",
            description = "Возвращает полную информацию об услуге: название, описание, стоимость, длительность, клинику"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Услуга найдена",
                    content = @Content(schema = @Schema(implementation = MedicalServiceDto.class))),
            @ApiResponse(responseCode = "404", description = "Услуга не найдена",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/{msId}")
    public MedicalServiceDto findMedicalServiceById(
            @Parameter(description = "ID медицинской услуги", required = true, example = "1")
            @PathVariable Long msId) {
        log.info("Public API: получение услуги с ID: {}", msId);
        return service.findMedicalServiceById(msId);
    }
}