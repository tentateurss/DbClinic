package ru.tentateursss.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class DoctorStatsDto {
    private Long doctorId;
    private String fullName;
    private Long totalAppointments;
}
