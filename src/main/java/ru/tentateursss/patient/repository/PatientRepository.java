package ru.tentateursss.patient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tentateursss.patient.model.Patient;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findByClinicId(Long clinicId);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    Optional<Patient> findByPhone(String phone);

    Optional<Patient> findByEmail(String email);

    List<Patient> findByFullNameContainingIgnoreCase(String fullName);
}