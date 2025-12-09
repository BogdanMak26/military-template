package ua.edu.viti.military.exception;

// Використовується, коли номер авто або військовий квиток дублюється
public class DuplicateResourceException extends BaseException {
  public DuplicateResourceException(String message) {
    super(message);
  }
}