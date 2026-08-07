package ru.tentateursss.medicalservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewMedicalServiceDto {

    @NotBlank(message = "Название услуги не может быть пустым")
    private String title;

    @NotBlank(message = "Описание услуги не может быть пустым")
    private String description;

    @NotNull(message = "Цена услуги обязательна")
    @Positive(message = "Цена должна быть больше 0")
    private int cost;

    @NotNull(message = "Длительность услуги обязательна")
    @Positive(message = "Длительность должна быть больше 0")
    private Integer durationMinutes;
    @NotNull(message = "ID клиники обязателен")
    private Long clinicId;
}