package ru.tentateursss.appointment.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewAppointmentDto {

    @NotNull(message = "ID пациента обязательно")
    private Long patientId;

    @NotNull(message = "ID сотрудника обязателен")
    private Long employeeId;

    @NotNull(message = "ID клиники обязателен")
    private Long clinicId;

    @NotNull(message = "Дата и время обязательны")
    @FutureOrPresent(message = "Дата должна быть в будущем")
    private LocalDateTime dateTime;

    @NotNull(message = "ID услуги обязателен")
    private Long medicalServiceId;

    private Boolean isPaid;

    private String notes;
}