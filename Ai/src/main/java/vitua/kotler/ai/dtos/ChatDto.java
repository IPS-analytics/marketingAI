package vitua.kotler.ai.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для представления чата")
public class ChatDto {

    @Schema(description = "Уникальный идентификатор чата", example = "1")
    private Long id;

    @Schema(description = "ID пользователя-владельца чата", example = "123", required = true)
    private Long idUser;

    @Schema(description = "Название чата", example = "Общий чат поддержки", required = true)
    private String chatName;
}