package vitua.kotler.ai.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO для запроса аутентификации пользователя")
public class SignInRequestDto {

    @Schema(description = "Имя пользователя", example = "ivan_ivanov", required = true)
    @Size(max = 50, message = "Имя пользователя должно содержать до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;

    @Schema(description = "Пароль пользователя", example = "mySecurePassword123", required = true)
    @Size(max = 255, message = "Длина пароля должна быть не более 255 символов")
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
}