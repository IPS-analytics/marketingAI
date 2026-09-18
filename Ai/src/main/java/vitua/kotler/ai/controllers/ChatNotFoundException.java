package vitua.kotler.ai.controllers;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(Long chatId) {
        super("Чат с ID " + chatId + " не найден");
    }
}
