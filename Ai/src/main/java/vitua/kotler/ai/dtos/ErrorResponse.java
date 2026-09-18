package vitua.kotler.ai.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO для стандартизированных ответов об ошибках.
 * Используется для возврата структурированной информации об ошибках в API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для стандартизированного ответа об ошибке")
public class ErrorResponse {

    @Schema(
            description = "Время возникновения ошибки",
            example = "2024-01-15T14:30:45.123456",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime timestamp;

    @Schema(
            description = "HTTP статус код ошибки",
            example = "404",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private int status;

    @Schema(
            description = "Тип ошибки",
            example = "Not Found",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String error;

    @Schema(
            description = "Детальное сообщение об ошибке",
            example = "Чат с ID 5 не найден",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String message;

    @Schema(
            description = "Путь API, по которому произошла ошибка",
            example = "/api/chats/5/messages",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String path;

    /**
     * Конструктор для создания ответа об ошибке.
     *
     * @param status HTTP статус код
     * @param error тип ошибки
     * @param message детальное сообщение
     * @param path путь API
     */
    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}