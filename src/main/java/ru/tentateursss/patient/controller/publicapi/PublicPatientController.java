package ru.tentateursss.patient.controller.publicapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.tentateursss.patient.dto.PatientDto;
import ru.tentateursss.patient.service.PatientService;
import ru.tentateursss.exception.Error;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/public/patients")
@RequiredArgsConstructor
@Validated
@Tag(name = "Public: Пациенты", description = "Получение информации о пациентах: поиск по клинике и ID")
public class PublicPatientController {

    private final PatientService patientService;

    @Operation(
            summary = "Получить пациента по ID",
            description = "Возвращает полную информацию о пациенте: ФИО, контакты, дату рождения, медкарту, заметки, клинику"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пациент найден",
                    content = @Content(schema = @Schema(implementation = PatientDto.class))),
            @ApiResponse(responseCode = "404", description = "Пациент не найден",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/{patientId}")
    public PatientDto getPatient(
            @Parameter(description = "ID пациента", required = true, example = "1")
            @PathVariable Long patientId) {
        log.info("Public API: Получение пациента: {}", patientId);
        return patientService.getPatient(patientId);
    }

    @Operation(
            summary = "Получить список всех пациентов с пагинацией и сортировкой",
            description = "Возвращает список всех пациентов всех клиник постранично. По умолчанию 20 пациентов на странице. Сортировка: поле,направление"
    )
    @ApiResponse(responseCode = "200", description = "Страница со списком пациентов")
    @GetMapping
    public Page<PatientDto> getPatients(
            @Parameter(description = "Номер страницы (начиная с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Количество пациентов на странице", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Сортировка в формате поле,направление (например, fullName,asc или registrationDate,desc)", example = "fullName,asc")
            @RequestParam(defaultValue = "fullName,asc") String sort) {
        log.info("Public API: Получение всех пациентов (страница {}, размер {}, сортировка {})", page, size, sort);
        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = Sort.Direction.fromString(parts[1]);
        return patientService.getAllPatients(PageRequest.of(page, size, Sort.by(direction, field)));
    }

    @Operation(
            summary = "Пациенты конкретной клиники",
            description = "Возвращает список всех пациентов, прикреплённых к указанной клинике"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пациентов клиники (может быть пустым)"),
            @ApiResponse(responseCode = "404", description = "Клиника не найдена",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/clinic/{clinicId}")
    public List<PatientDto> getPatientsByClinic(
            @Parameter(description = "ID клиники", required = true, example = "1")
            @PathVariable Long clinicId) {
        log.info("Public API: Получение пациентов из клиники: {}", clinicId);
        return patientService.getAllPatientsByClinicId(clinicId);
    }

    @Operation(
            summary = "Получить пациента по email",
            description = "Возвращает пациента по email"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пациент с указанной почтой"),
            @ApiResponse(responseCode = "404", description = "Пациент не найден",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/email")
    public PatientDto getPatientByEmail(
            @Parameter(description = "Email пациента", required = true)
            @RequestParam String email) {
        log.info("Public API: Получение пациента с почтой: {}", email);
        return patientService.getPatientByEmail(email);
    }

    @Operation(
            summary = "Получить пациента по телефону",
            description = "Возвращает пациента по телефону"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пациент с указанным номером"),
            @ApiResponse(responseCode = "404", description = "Пациент не найден",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/phone")
    public PatientDto getPatientByPhone(
            @Parameter(description = "Телефон пациента", required = true)
            @RequestParam String phone) {
        log.info("Public API: Получение пациента с номером: {}", phone);
        return patientService.getPatientByPhone(phone);
    }

    @Operation(
            summary = "Получить пациента по ФИО",
            description = "Возвращает пациента по ФИО"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пациент с указанным ФИО"),
            @ApiResponse(responseCode = "404", description = "Пациент не найден",
                    content = @Content(schema = @Schema(implementation = Error.class)))
    })
    @GetMapping("/search")
    public List<PatientDto> getPatientByFullName(
            @Parameter(description = "ФИО пациента", required = true)
            @RequestParam String fullName) {
        log.info("Public API: Получение пациента с ФИО: {}", fullName);
        return patientService.getPatientByFullName(fullName);
    }
}