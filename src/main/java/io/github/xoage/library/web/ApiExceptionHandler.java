package io.github.xoage.library.web;

import io.github.xoage.library.service.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/// Превращает исключения из всех контроллеров в понятные ответы с ошибкой.
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /// Нарушены ограничения базы. Код PostgreSQL лежит внутри исходного SQLException.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException e) {
        String state = e.getMostSpecificCause() instanceof SQLException sql && sql.getSQLState() != null
                ? sql.getSQLState()
                : "";
        return switch (state) {
            case "23505" -> ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                    "такая запись уже есть: автор с таким именем или книга с таким ISBN");
            case "23503" -> ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                    "нет автора с таким номером");
            case "23514" -> ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                    "пустое имя или название, либо год не от 1450 до 2100");
            case "22001" -> ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                    "слишком длинное значение: имя до 50 символов, название до 100, ISBN до 17");
            default -> ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                    "данные не соответствуют ограничениям базы");
        };
    }
}
