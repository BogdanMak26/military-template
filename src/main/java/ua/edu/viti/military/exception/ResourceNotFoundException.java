package ua.edu.viti.military.exception;

// Використовується, коли не знайдено Транспорт, Водія або Категорію
public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}