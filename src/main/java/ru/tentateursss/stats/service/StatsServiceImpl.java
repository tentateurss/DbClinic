package ru.tentateursss.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tentateursss.appointment.model.Appointment;
import ru.tentateursss.appointment.repository.AppointmentRepository;
import ru.tentateursss.clinic.repository.ClinicRepository;
import ru.tentateursss.employee.model.Employee;
import ru.tentateursss.employee.repository.EmployeeRepository;
import ru.tentateursss.enums.AppointmentStatus;
import ru.tentateursss.enums.EmployeeRole;
import ru.tentateursss.exception.NotFoundException;
import ru.tentateursss.patient.repository.PatientRepository;
import ru.tentateursss.stats.dto.ClinicSummaryDto;
import ru.tentateursss.stats.dto.DoctorStatsDto;
import ru.tentateursss.stats.mapper.StatsMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final EmployeeRepository employeeRepository;
    private final ClinicRepository clinicRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    @Override
    public Map<EmployeeRole, Long> getEmployeeCountByRole(Long clinicId) {
        clinicRepository.findById(clinicId)
                .orElseThrow(() -> new NotFoundException("Клиника с ID " + clinicId + " не найдена"));

        Map<EmployeeRole, Long> stats = employeeRepository.findByClinicId(clinicId).stream()
                .collect(Collectors.groupingBy(Employee::getRole, Collectors.counting()));

        log.info("Статистика по ролям для клиники {}: {}", clinicId, stats);
        return stats;
    }

    @Override
    public Map<AppointmentStatus, Long> getAppointmentCountByStatus(Long clinicId) {
        clinicRepository.findById(clinicId)
                .orElseThrow(() -> new NotFoundException("Клиника с ID " + clinicId + " не найдена"));

        Map<AppointmentStatus, Long> stats = appointmentRepository.findByClinicId(clinicId).stream()
                .collect(Collectors.groupingBy(Appointment::getStatus, Collectors.counting()));

        log.info("Статистика по статусам записи для клиники {}: {}", clinicId, stats);
        return stats;
    }

    @Override
    public List<DoctorStatsDto> getDoctorStats(Long clinicId) {
        clinicRepository.findById(clinicId)
                .orElseThrow(() -> new NotFoundException("Клиника с ID " + clinicId + " не найдена"));

        List<Employee> doctors = employeeRepository.findByClinicId(clinicId).stream()
                .filter(employee -> employee.getRole().equals(EmployeeRole.DOCTOR))
                .toList();

        List<DoctorStatsDto> result = doctors.stream()
                .map(doctor -> StatsMapper.toDoctorDto(doctor,
                        appointmentRepository.countByEmployeeId(doctor.getId())))
                .collect(Collectors.toList());

        log.info("Статистика по докторам для клиники {}: {}", clinicId, result);
        return result;
    }

    @Override
    public Long getPatientCountByClinicId(Long clinicId) {
        clinicRepository.findById(clinicId)
                .orElseThrow(() -> new NotFoundException("Клиника с ID " + clinicId + " не найдена"));

        Long count = patientRepository.countByClinicId(clinicId);
        log.info("Количество пациентов в клинике {}: {}", clinicId, count);
        return count;
    }

    @Override
    public ClinicSummaryDto getClinicSummary(Long clinicId) {
        clinicRepository.findById(clinicId)
                .orElseThrow(() -> new NotFoundException("Клиника с ID " + clinicId + " не найдена"));

        List<Employee> employees = employeeRepository.findByClinicId(clinicId);
        long totalDoctors = employees.stream()
                .filter(e -> e.getRole().equals(EmployeeRole.DOCTOR)).count();

        List<Appointment> appointments = appointmentRepository.findByClinicId(clinicId);

        return ClinicSummaryDto.builder()
                .totalEmployees(employees.size())
                .totalDoctors(totalDoctors)
                .totalPatients(patientRepository.countByClinicId(clinicId))
                .totalAppointments(appointments.size())
                .scheduledAppointments(countByStatus(appointments, AppointmentStatus.SCHEDULED))
                .confirmedAppointments(countByStatus(appointments, AppointmentStatus.CONFIRMED))
                .completedAppointments(countByStatus(appointments, AppointmentStatus.COMPLETED))
                .cancelledAppointments(countByStatus(appointments, AppointmentStatus.CANCELLED))
                .noShowAppointments(countByStatus(appointments, AppointmentStatus.NO_SHOW))
                .build();
    }

    @Override
    public ClinicSummaryDto getOverallSummary() {
        List<Employee> employees = employeeRepository.findAll();
        long totalDoctors = employees.stream()
                .filter(e -> e.getRole().equals(EmployeeRole.DOCTOR)).count();

        List<Appointment> appointments = appointmentRepository.findAll();

        return ClinicSummaryDto.builder()
                .totalEmployees(employees.size())
                .totalDoctors(totalDoctors)
                .totalPatients(patientRepository.count())
                .totalAppointments(appointments.size())
                .scheduledAppointments(countByStatus(appointments, AppointmentStatus.SCHEDULED))
                .confirmedAppointments(countByStatus(appointments, AppointmentStatus.CONFIRMED))
                .completedAppointments(countByStatus(appointments, AppointmentStatus.COMPLETED))
                .cancelledAppointments(countByStatus(appointments, AppointmentStatus.CANCELLED))
                .noShowAppointments(countByStatus(appointments, AppointmentStatus.NO_SHOW))
                .build();
    }

    private long countByStatus(List<Appointment> appointments, AppointmentStatus status) {
        return appointments.stream().filter(a -> a.getStatus().equals(status)).count();
    }
}
