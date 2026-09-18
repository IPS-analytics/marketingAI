package vitua.kotler.ai.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO для запроса обновления токена")
public class RefreshRequestDto {

    @Schema(description = "Refresh токен для получения нового access токена",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            required = true)
    @NotBlank(message = "Refresh токен не может быть пустым")
    private String refreshToken;
}