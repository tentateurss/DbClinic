package ru.tentateursss.clinic.repository;

import org.springframework.data.jpa.repository.Query;
import ru.tentateursss.appointment.model.Appointment;
import ru.tentateursss.clinic.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tentateursss.employee.model.Employee;
import ru.tentateursss.patient.model.Patient;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {

    @Query("SELECT e FROM Employee e WHERE e.clinic.id = ?1")
    List<Employee> findAllEmployeesById(Long clinicId);

    @Query("SELECT p FROM Patient p WHERE p.clinic.id = ?1")
    List<Patient> findAllPatientsById(Long clinicId);

    @Query("SELECT a FROM Appointment a WHERE a.clinic.id =?1")
    List<Appointment> findAllAppointmentsById(Long clinicId);

    Optional<Clinic> findById(Long clinicId);

    void deleteById(Long clinicId);

    boolean existsById(Long clinicId);

    boolean existsByInn(String inn);
}
