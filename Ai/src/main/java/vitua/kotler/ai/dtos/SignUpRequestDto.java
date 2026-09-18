package vitua.kotler.ai.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO для запроса на регистрацию нового пользователя.
 * <p>
 * Содержит необходимые данные для создания нового аккаунта: имя пользователя, email и пароль.
 */
@Data
public class SignUpRequestDto {

    /**
     * Имя пользователя.
     */
    @Size(max = 50, message = "Имя пользователя должно содержать до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String username;

    /**
     * Адрес электронной почты пользователя.
     */
    @Size(min = 5, max = 255, message = "Адрес электронной почты должен содержать от 5 до 255 символов")
    @NotBlank(message = "Адрес электронной почты не может быть пустыми")
    @Email(message = "Email адрес должен быть в формате user@example.com")
    private String email;

    /**
     * Пароль пользователя.
     */
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, max = 255, message = "Длина пароля должна быть от 6 до 255 символов")
    private String password;
}