package ru.tentateursss.appointment.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.tentateursss.appointment.model.Appointment;
import ru.tentateursss.appointment.repository.AppointmentRepository;
import ru.tentateursss.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppointmentScheduler {

    private final AppointmentRepository appointmentRepository;

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void markNoShows() {
        List<Appointment> confirmedAppointments = appointmentRepository.findByStatus(AppointmentStatus.CONFIRMED);
        LocalDateTime now = LocalDateTime.now();

        List<Appointment> missed = confirmedAppointments.stream()
                .filter(a -> a.getDateTime().isBefore(now))
                .peek(a -> a.setStatus(AppointmentStatus.NO_SHOW))
                .toList();

        if (!missed.isEmpty()) {
            log.info("Отмечено как неявка {} записей", missed.size());
            missed.forEach(a -> log.debug("Запись ID: {}, время: {}", a.getId(), a.getDateTime()));
        }
    }
}
