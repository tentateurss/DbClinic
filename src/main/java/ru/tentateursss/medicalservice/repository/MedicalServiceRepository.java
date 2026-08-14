package ru.tentateursss.medicalservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import ru.tentateursss.medicalservice.model.MedicalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicalServiceRepository extends JpaRepository<MedicalService, Integer> {

    @Query("SELECT ms FROM MedicalService ms WHERE ms.id = ?1")
    Optional<MedicalService> findById(Long id);

    Page<MedicalService> findByClinicId(Long clinicId, Pageable pageable);

    void deleteById(Long id);

    boolean existsById(Long id);
}