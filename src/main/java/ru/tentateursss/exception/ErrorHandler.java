package ru.tentateursss.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error handleNotFound(NotFoundException e) {
        log.warn("Не найдено: {}", e.getMessage());
        return new Error("Не найдено: " + e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error handleConflict(ConflictException e) {
        log.warn("Конфликт: {}", e.getMessage());
        return new Error("Конфликт: " + e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DateTimeConflict.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error handleDateTimeConflict(DateTimeConflict e) {
        log.warn("Конфликт даты: {}", e.getMessage());
        return new Error("Конфликт даты: " + e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ValidateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleValidation(ValidateException e) {
        log.warn("Ошибка валидации: {}", e.getMessage());
        return new Error("Ошибка валидации: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Ошибка валидации: {}", message);
        return new Error("Ошибка валидации: " + message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("Неверный формат запроса: {}", e.getMessage());
        return new Error("Неверный формат запроса", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Error handleGeneral(Exception e) {
        log.error("Неизвестная ошибка: {}", e.getMessage(), e);
        return new Error("Внутренняя ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}