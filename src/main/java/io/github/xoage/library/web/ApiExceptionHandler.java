package io.github.xoage.library.web;

import io.github.xoage.library.service.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/// Превращает исключения из всех контроллеров в понятные ответы с ошибкой.
/// Стандартные ошибки Spring MVC (неверный JSON, id не числом) обрабатывает родитель.
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler { //получаем готовую обработку всех стандартных ошибок Spring в формате ProblemDetail

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

    /// Ошибки @Valid: все неверные поля сразу, в свойстве errors.
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "неверные данные в запросе");
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
    }
}
