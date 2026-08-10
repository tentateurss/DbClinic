package ru.tentateursss.appointment.repository;

import ru.tentateursss.appointment.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tentateursss.employee.model.Employee;
import ru.tentateursss.enums.AppointmentStatus;
import ru.tentateursss.patient.model.Patient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findById(Long id);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByEmployeeId(Long employeeId);

    List<Appointment> findByClinicId(Long clinicId);

    List<Appointment> findByMedicalServiceId(Long medicalServiceId);

    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status);

    List<Appointment> findByEmployeeIdAndStatus(Long employeeId, AppointmentStatus status);

    List<Appointment> findByClinicIdAndStatus(Long clinicId, AppointmentStatus status);

    List<Appointment> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);

    Optional<Appointment> findByEmployeeIdAndDateTime(Long employeeId, LocalDateTime date);

    List<Appointment> findByEmployeeIdAndDateTimeBetween(Long employeeId, LocalDateTime start, LocalDateTime end);

    List<Appointment> findByPatientIdAndDateTimeBetween(Long patientId, LocalDateTime start, LocalDateTime end);

    boolean existsByEmployeeAndStatusIn(Employee employee, List<AppointmentStatus> statuses);

    boolean existsByPatientAndStatusIn(Patient patient, List<AppointmentStatus> statuses);

    boolean existsByClinicIdAndStatusIn(Long clinicId, List<AppointmentStatus> statuses);

    boolean existsByEmployeeIdAndDateTime(Long employeeId, LocalDateTime dateTime);

    boolean existsByPatientIdAndDateTime(Long patientId, LocalDateTime dateTime);

    long countByStatus(AppointmentStatus status);

    long countByPatientId(Long patientId);

    long countByEmployeeId(Long employeeId);
}