package ru.tentateursss.stats.service;

import ru.tentateursss.enums.AppointmentStatus;
import ru.tentateursss.enums.EmployeeRole;
import ru.tentateursss.stats.dto.ClinicSummaryDto;
import ru.tentateursss.stats.dto.DoctorStatsDto;

import java.util.List;
import java.util.Map;

public interface StatsService {

    Map<EmployeeRole, Long> getEmployeeCountByRole(Long clinicId);

    Map<AppointmentStatus, Long> getAppointmentCountByStatus(Long clinicId);

    List<DoctorStatsDto> getDoctorStats(Long clinicId);

    Long getPatientCountByClinicId(Long clinicId);

    ClinicSummaryDto getClinicSummary(Long clinicId);

    ClinicSummaryDto getOverallSummary();
}
