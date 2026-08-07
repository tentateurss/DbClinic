package ru.tentateursss.medicalservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import ru.tentateursss.clinic.model.Clinic;

@Entity
@Table(name = "medical_service")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medical_service_id")
    private Long id;

    @NotBlank(message = "Название услуги не может быть пустым")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Описание услуги не может быть пустым")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Цена услуги обязательна")
    @Positive(message = "Цена должна быть больше 0")
    @Column(nullable = false)
    private int cost;

    @NotNull(message = "Длительность услуги обязательна")
    @Positive(message = "Длительность должна быть больше 0")
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;
}