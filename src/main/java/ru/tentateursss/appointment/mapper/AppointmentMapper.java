package ru.tentateursss.appointment.mapper;

import lombok.experimental.UtilityClass;
import ru.tentateursss.appointment.dto.AppointmentDto;
import ru.tentateursss.appointment.dto.NewAppointmentDto;
import ru.tentateursss.appointment.model.Appointment;
import ru.tentateursss.clinic.mapper.ClinicMapper;
import ru.tentateursss.clinic.model.Clinic;
import ru.tentateursss.employee.mapper.EmployeeMapper;
import ru.tentateursss.employee.model.Employee;
import ru.tentateursss.enums.AppointmentStatus;
import ru.tentateursss.medicalservice.mapper.MedicalServiceMapper;
import ru.tentateursss.medicalservice.model.MedicalService;
import ru.tentateursss.patient.mapper.PatientMapper;
import ru.tentateursss.patient.model.Patient;

@UtilityClass
public class AppointmentMapper {

    public AppointmentDto toDto(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        return new AppointmentDto(
                appointment.getId(),
                PatientMapper.toDto(appointment.getPatient()),
                EmployeeMapper.toDto(appointment.getEmployee()),
                ClinicMapper.toDto(appointment.getClinic()),
                appointment.getDateTime(),
                MedicalServiceMapper.toDto(appointment.getMedicalService()),
                appointment.getIsPaid(),
                appointment.getNotes(),
                appointment.getStatus(),
                appointment.getCreatedAt(),
                appointment.getCost());
    }

    public Appointment toEntity(NewAppointmentDto dto,
                                Patient patient,
                                Employee employee,
                                Clinic clinic,
                                MedicalService medicalService) {
        if (dto == null) {
            return null;
        }

        return Appointment.builder()
                .patient(patient)
                .employee(employee)
                .clinic(clinic)
                .dateTime(dto.getDateTime())
                .medicalService(medicalService)
                .isPaid(dto.getIsPaid() != null && dto.getIsPaid())
                .notes(dto.getNotes())
                .status(AppointmentStatus.SCHEDULED)
                .build();
    }

    public void updateEntity(Appointment appointment,
                             NewAppointmentDto dto) {
        if (appointment == null || dto == null) {
            return;
        }

        appointment.setDateTime(dto.getDateTime());
        appointment.setIsPaid(dto.getIsPaid() != null && dto.getIsPaid());
        appointment.setNotes(dto.getNotes());
    }

    public void updateStatus(Appointment appointment, AppointmentStatus newStatus) {
        if (appointment == null || newStatus == null) {
            return;
        }
        appointment.setStatus(newStatus);
    }
}
