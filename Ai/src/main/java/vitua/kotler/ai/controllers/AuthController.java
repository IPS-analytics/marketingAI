package vitua.kotler.ai.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vitua.kotler.ai.dtos.JwtAuthenticationResponse;
import vitua.kotler.ai.dtos.SignUpRequestDto;
import vitua.kotler.ai.services.AuthenticationService;
import vitua.kotler.ai.dtos.SignInRequestDto;
import vitua.kotler.ai.dtos.RefreshRequestDto;

/**
 * Контроллер для обработки операций аутентификации пользователей.
 * Предоставляет эндпоинты для регистрации, входа в систему и обновления токена.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API для аутентификации и управления пользователями")
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * Регистрирует нового пользователя на основе переданных данных.
     *
     * @param request DTO с информацией для регистрации пользователя
     * @return {@link JwtAuthenticationResponse} — объект, содержащий JWT access/refresh токены
     */
    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создает нового пользователя в системе и возвращает JWT токены для аутентификации"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная регистрация",
                    content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидные данные или пользователь уже существует"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера"
            )
    })
    @PostMapping("/reg")
    public JwtAuthenticationResponse signUp(
            @Parameter(description = "Данные для регистрации пользователя", required = true)
            @RequestBody @Valid SignUpRequestDto request) {
        return authenticationService.signUp(request);
    }

    /**
     * Аутентифицирует пользователя на основе логина и пароля.
     *
     * @param request DTO с учетными данными пользователя
     * @return {@link JwtAuthenticationResponse} — объект с новыми JWT access/refresh токенами
     */
    @Operation(
            summary = "Аутентификация пользователя",
            description = "Проверяет учетные данные пользователя и возвращает JWT токены для доступа к API"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешная аутентификация",
                    content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Неверные учетные данные"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидные данные запроса"
            )
    })
    @PostMapping("/login")
    public JwtAuthenticationResponse signIn(
            @Parameter(description = "Учетные данные пользователя", required = true)
            @RequestBody @Valid SignInRequestDto request) {
        return authenticationService.signIn(request);
    }

    /**
     * Обновляет access-токен по действующему refresh-токену.
     *
     * @param request DTO с refresh-токеном
     * @return {@link JwtAuthenticationResponse} — новый access/refresh токен
     */
    @Operation(
            summary = "Обновление JWT токена",
            description = "Обновляет access токен с помощью валидного refresh токена"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Токены успешно обновлены",
                    content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Невалидный или просроченный refresh токен"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидные данные запроса"
            )
    })
    @PostMapping("/refresh")
    public JwtAuthenticationResponse refresh(
            @Parameter(description = "Refresh токен для обновления", required = true)
            @RequestBody @Valid RefreshRequestDto request) {
        return authenticationService.refresh(request);
    }
}