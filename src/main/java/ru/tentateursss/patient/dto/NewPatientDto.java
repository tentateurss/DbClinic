package ru.tentateursss.patient.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewPatientDto {

    @NotBlank(message = "ФИО пациента не может быть пустым")
    private String fullName;

    @NotBlank(message = "Номер телефона пациента не может быть пустым")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Номер телефона введен некорректно")
    private String phone;

    @NotBlank(message = "Электронная почта пациента не может быть пустой")
    @Email(message = "Почта введена некорректно")
    private String email;

    @NotNull(message = "Дата рождения должна быть указана")
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthDate;

    @NotBlank(message = "Номер медицинской карты не может быть пустым")
    private String medicalCardNumber;

    private String notes;

    @NotNull(message = "ID клиники обязателен")
    private Long clinicId;
}