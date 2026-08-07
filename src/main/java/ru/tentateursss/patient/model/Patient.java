package ru.tentateursss.patient.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import ru.tentateursss.clinic.model.Clinic;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "patient")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long id;

    @NotBlank(message = "ФИО пациента не может быть пустым")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotBlank(message = "Номер телефона пациента не может быть пустым")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Номер телефона введен некорректно")
    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @NotBlank(message = "Электронная почта пациента не может быть пустой")
    @Email(message = "Почта введена некорректно")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @CreationTimestamp
    @Column(name = "registration_date", updatable = false)
    private LocalDate registrationDate;

    @Column(name = "medical_card_number")
    private String medicalCardNumber;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    public int getAge() {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
