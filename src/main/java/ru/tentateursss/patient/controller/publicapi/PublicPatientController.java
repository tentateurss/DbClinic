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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
            summary = "Получить список всех пациентов",
            description = "Возвращает список всех пациентов всех клиник"
    )
    @ApiResponse(responseCode = "200", description = "Список всех пациентов (может быть пустым)")
    @GetMapping
    public List<PatientDto> getPatients() {
        log.info("Public API: Получение всех пациентов");
        return patientService.getAllPatients();
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
}