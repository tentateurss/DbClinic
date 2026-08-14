package ru.tentateursss.appointment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<Appointment> findByPatientId(Long patientId, Pageable pageable);

    Page<Appointment> findByEmployeeId(Long employeeId, Pageable pageable);

    Page<Appointment> findByClinicId(Long clinicId, Pageable pageable);

    List<Appointment> findByClinicId(Long clinicId);

    Page<Appointment> findByMedicalServiceId(Long medicalServiceId, Pageable pageable);

    Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);

    List<Appointment> findByStatus(AppointmentStatus status);

    Page<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status, Pageable pageable);

    Page<Appointment> findByEmployeeIdAndStatus(Long employeeId, AppointmentStatus status, Pageable pageable);

    Page<Appointment> findByClinicIdAndStatus(Long clinicId, AppointmentStatus status, Pageable pageable);

    Page<Appointment> findByDateTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Appointment> findByEmployeeIdAndDateTimeBetween(Long employeeId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Appointment> findByEmployeeIdAndDateTimeBetween(Long employeeId, LocalDateTime start, LocalDateTime end);

    Page<Appointment> findByStatusIn(List<AppointmentStatus> status, Pageable pageable);

    boolean existsByEmployeeAndStatusIn(Employee employee, List<AppointmentStatus> statuses);

    boolean existsByPatientAndStatusIn(Patient patient, List<AppointmentStatus> statuses);

    boolean existsByClinicIdAndStatusIn(Long clinicId, List<AppointmentStatus> statuses);

    boolean existsByEmployeeIdAndDateTime(Long employeeId, LocalDateTime dateTime);

    boolean existsByPatientIdAndDateTime(Long patientId, LocalDateTime dateTime);

    long countByStatus(AppointmentStatus status);

    long countByPatientId(Long patientId);

    long countByEmployeeId(Long employeeId);
}