package ua.edu.viti.military.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. Обробка, коли щось не знайдено (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            WebRequest request) {

        log.error("Resource not found: {}", ex.getMessage());
        return buildResponse(ex, HttpStatus.NOT_FOUND, "Resource Not Found", request);
    }

    // 2. Обробка дублікатів (409)
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex,
            WebRequest request) {

        log.error("Duplicate resource: {}", ex.getMessage());
        return buildResponse(ex, HttpStatus.CONFLICT, "Duplicate Resource", request);
    }

    // 3. Обробка бізнес-правил (400)
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> handleBusinessLogic(
            BusinessLogicException ex,
            WebRequest request) {

        log.error("Business logic error: {}", ex.getMessage());
        return buildResponse(ex, HttpStatus.BAD_REQUEST, "Business Logic Violation", request);
    }

    // 4. Обробка помилок валідації @Valid (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            validationErrors.put(error.getField(), error.getDefaultMessage());
        });

        log.error("Validation errors: {}", validationErrors);

        ErrorResponse error = new ErrorResponse();
        error.setType("/errors/validation");
        error.setTitle("Validation Failed");
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setDetail("Помилки валідації вхідних даних");
        error.setInstance(request.getDescription(false).replace("uri=", ""));
        error.setTimestamp(LocalDateTime.now());
        error.setErrors(validationErrors); // Додаємо список конкретних полів

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 5. Всі інші помилки (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(
            Exception ex,
            WebRequest request) {

        log.error("Unexpected error occurred", ex);

        ErrorResponse error = new ErrorResponse();
        error.setType("/errors/internal");
        error.setTitle("Internal Server Error");
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setDetail("Сталася внутрішня помилка сервера. Зверніться до адміністратора.");
        error.setInstance(request.getDescription(false).replace("uri=", ""));
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // Допоміжний метод для побудови відповіді
    private ResponseEntity<ErrorResponse> buildResponse(
            Exception ex,
            HttpStatus status,
            String title,
            WebRequest request) {

        ErrorResponse error = new ErrorResponse();
        error.setType("/errors/" + title.toLowerCase().replace(" ", "-"));
        error.setTitle(title);
        error.setStatus(status.value());
        error.setDetail(ex.getMessage());
        error.setInstance(request.getDescription(false).replace("uri=", ""));
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(status).body(error);
    }
}