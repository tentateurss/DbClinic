package ru.tentateursss.stats.mapper;

import lombok.experimental.UtilityClass;
import ru.tentateursss.employee.model.Employee;
import ru.tentateursss.stats.dto.DoctorStatsDto;

@UtilityClass
public class StatsMapper {

    public DoctorStatsDto toDoctorDto(Employee employee, long totalAppointments) {
        return DoctorStatsDto.builder()
                .doctorId(employee.getId())
                .fullName(employee.getFullName())
                .totalAppointments(totalAppointments)
                .build();
    }
}
