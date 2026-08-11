package ru.tentateursss.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ClinicSummaryDto {
    private long totalEmployees;
    private long totalDoctors;
    private long totalPatients;
    private long totalAppointments;
    private long scheduledAppointments;
    private long confirmedAppointments;
    private long completedAppointments;
    private long cancelledAppointments;
    private long noShowAppointments;
}