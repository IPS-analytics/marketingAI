package vitua.kotler.ai.controllers;

/**
 * Исключение, выбрасываемое, когда пользователь не найден.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Пользователь с ID " + id + " не найден.");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}