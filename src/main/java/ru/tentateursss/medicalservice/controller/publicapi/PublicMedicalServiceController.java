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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
            summary = "Получить список всех услуг с пагинацией и сортировкой",
            description = "Возвращает все медицинские услуги во всех клиниках постранично. По умолчанию 20 услуг на странице. Сортировка: поле,направление"
    )
    @ApiResponse(responseCode = "200", description = "Страница со списком услуг")
    @GetMapping
    public Page<MedicalServiceDto> findAllMedicalService(
            @Parameter(description = "Номер страницы (начиная с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Количество услуг на странице", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Сортировка в формате поле,направление (например, title,asc или cost,desc)", example = "title,asc")
            @RequestParam(defaultValue = "title,asc") String sort) {
        log.info("Public API: получение всех услуг (страница {}, размер {}, сортировка {})", page, size, sort);
        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = Sort.Direction.fromString(parts[1]);
        return service.findAllMedicalService(PageRequest.of(page, size, Sort.by(direction, field)));
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