package ru.tentateursss.clinic.repository;

import ru.tentateursss.clinic.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {

    Optional<Clinic> findById(Long clinicId);

    void deleteById(Long clinicId);

    boolean existsById(Long clinicId);

    boolean existsByInn(String inn);
}
