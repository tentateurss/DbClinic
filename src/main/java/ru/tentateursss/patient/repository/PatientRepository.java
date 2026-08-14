package ru.tentateursss.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tentateursss.patient.model.Patient;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Page<Patient> findByClinicId(Long clinicId, Pageable pageable);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByClinicId(Long clinicId);

    Long countByClinicId(Long clinicId);

    Optional<Patient> findByPhone(String phone);

    Optional<Patient> findByEmail(String email);

    Page<Patient> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);
}