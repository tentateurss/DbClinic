package ru.tentateursss.stats.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tentateursss.enums.AppointmentStatus;
import ru.tentateursss.enums.EmployeeRole;
import ru.tentateursss.stats.dto.ClinicSummaryDto;
import ru.tentateursss.stats.dto.DoctorStatsDto;
import ru.tentateursss.stats.service.StatsService;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/stats")
@RequiredArgsConstructor
@Tag(name = "Admin: получение статистики", description = "Получение различном статистики")
public class AdminStatsController {

    private final StatsService statsService;

    @Operation(
            summary = "Количество сотрудников по ролям в клинике",
            description = "Возвращает мапу: роль → количество сотрудников с этой ролью в указанной клинике"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статистика по ролям"),
            @ApiResponse(responseCode = "404", description = "Клиника не найдена")
    })
    @GetMapping("/clinics/role/{clinicId}")
    public Map<EmployeeRole, Long> getStatisticsByRoleAndClinicId(
            @Parameter(description = "ID клиники", required = true, example = "1")
            @PathVariable Long clinicId) {
        log.info("Admin stats API: получение статистики по ролям clinicId: {} по ролям", clinicId);
        return statsService.getEmployeeCountByRole(clinicId);
    }

    @Operation(
            summary = "Количество записей по статусам в клинике",
            description = "Возвращает мапу: статус → количество записей с этим статусом в указанной клинике"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статистика по статусам"),
            @ApiResponse(responseCode = "404", description = "Клиника не найдена")
    })
    @GetMapping("/clinics/appStatus/{clinicId}")
    public Map<AppointmentStatus, Long> getStatisticsByStatusAndClinicId(
            @Parameter(description = "ID клиники", required = true, example = "1")
            @PathVariable Long clinicId) {
        log.info("Admin stats API: получение статистики по статусам clinicId: {}", clinicId);
        return statsService.getAppointmentCountByStatus(clinicId);
    }

    @Operation(
            summary = "Загрузка врачей клиники",
            description = "Возвращает список врачей с общим количеством записей у каждого"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список врачей с количеством записей"),
            @ApiResponse(responseCode = "404", description = "Клиника не найдена")
    })
    @GetMapping("/clinics/doctors/{clinicId}")
    public List<DoctorStatsDto> getDoctorStats(
            @Parameter(description = "ID клиники", required = true, example = "1")
            @PathVariable Long clinicId) {
        log.info("Admin stats API: получение статистики по врачам clinicId: {}", clinicId);
        return statsService.getDoctorStats(clinicId);
    }

    @Operation(
            summary = "Количество пациентов в клинике",
            description = "Возвращает общее количество пациентов, прикреплённых к указанной клинике"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Количество пациентов"),
            @ApiResponse(responseCode = "404", description = "Клиника не найдена")
    })
    @GetMapping("/clinics/patients/{clinicId}")
    public long getPatientCountByClinicId(
            @Parameter(description = "ID клиники", required = true, example = "1")
            @PathVariable Long clinicId) {
        log.info("Admin stats API: получение количества пациентов clinicId: {}", clinicId);
        return statsService.getPatientCountByClinicId(clinicId);
    }

    @Operation(
            summary = "Сводная статистика по клинике",
            description = "Возвращает общие показатели: количество сотрудников, врачей, пациентов и записей по статусам"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сводка по клинике"),
            @ApiResponse(responseCode = "404", description = "Клиника не найдена")
    })
    @GetMapping("/summary/{clinicId}")
    public ClinicSummaryDto getClinicSummary(
            @Parameter(description = "ID клиники", required = true, example = "1")
            @PathVariable Long clinicId) {
        log.info("Admin stats API: сводная статистика по клинике {}", clinicId);
        return statsService.getClinicSummary(clinicId);
    }

    @Operation(
            summary = "Общая статистика по всей сети клиник",
            description = "Возвращает агрегированные показатели по всем клиникам"
    )
    @ApiResponse(responseCode = "200", description = "Общая сводка")
    @GetMapping("/summary")
    public ClinicSummaryDto getOverallSummary() {
        log.info("Admin stats API: общая статистика по сети");
        return statsService.getOverallSummary();
    }
}
