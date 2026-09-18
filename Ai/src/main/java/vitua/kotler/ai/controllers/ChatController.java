package vitua.kotler.ai.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vitua.kotler.ai.dtos.ChatDto;
import vitua.kotler.ai.dtos.ErrorResponse;
import vitua.kotler.ai.dtos.MessageDto;
import vitua.kotler.ai.dtos.MessageResponceDto;
import vitua.kotler.ai.entitys.enums.MessageType;
import vitua.kotler.ai.services.AiService;
import vitua.kotler.ai.services.ChatService;
import vitua.kotler.ai.config.SecurityUtils;

import java.util.List;

/**
 * Контроллер для управления чатами и сообщениями пользователей.
 * Предоставляет эндпоинты для создания чатов, отправки сообщений
 * и получения истории переписки.
 */
@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
@Tag(name = "Chat Management", description = "API для управления чатами и сообщениями")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

    private final ChatService chatService;
    private final AiService aiService;
    private final SecurityUtils securityUtils;

    /**
     * Создает новый чат для текущего аутентифицированного пользователя.
     *
     * @param chat DTO с данными чата
     * @return созданный чат
     */
    @Operation(
            summary = "Создать новый чат",
            description = "Создает новый чат для текущего пользователя. Пользователь автоматически становится владельцем чата."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Чат успешно создан",
                    content = @Content(schema = @Schema(implementation = ChatDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидные данные запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ChatDto createChat(
            @Parameter(description = "Данные для создания чата", required = true)
            @RequestBody ChatDto chat) {
        Long currentUserId = securityUtils.getCurrentUserId();
        chat.setIdUser(currentUserId);
        return chatService.createChat(chat);
    }

    /**
     * Получает список чатов конкретного пользователя.
     * Доступно только для владельца чатов.
     *
     * @param userId ID пользователя
     * @return список чатов пользователя
     */
    @Operation(
            summary = "Получить чаты пользователя по ID",
            description = "Возвращает список всех чатов указанного пользователя. " +
                    "Доступно только для владельца аккаунта."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список чатов успешно получен",
                    content = @Content(schema = @Schema(implementation = ChatDto[].class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Нельзя просматривать чаты другого пользователя",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/user/{userId}")
    public List<ChatDto> getUserChats(
            @Parameter(description = "ID пользователя", required = true, example = "1")
            @PathVariable Long userId) {
        Long currentUserId = securityUtils.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new UnauthorizedException("Нельзя просматривать чаты другого пользователя");
        }
        return chatService.getAllChatsByUser(userId);
    }

    /**
     * Получает список чатов текущего аутентифицированного пользователя.
     *
     * @return список чатов текущего пользователя
     */
    @Operation(
            summary = "Получить мои чаты",
            description = "Возвращает список всех чатов текущего аутентифицированного пользователя."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список чатов успешно получен",
                    content = @Content(schema = @Schema(implementation = ChatDto[].class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping
    public List<ChatDto> getMyChats() {
        Long currentUserId = securityUtils.getCurrentUserId();
        return chatService.getAllChatsByUser(currentUserId);
    }

    /**
     * Отправляет сообщение в указанный чат и возвращает ответ Котлера.
     * На вход принимается сообщение пользователя (messageType = USER_MESSAGE),
     * а в ответе возвращается сообщение Котлера (messageType = KOTLER_MESSAGE).
     *
     * @param chatId     ID чата
     * @param messageDto сообщение пользователя
     * @return сообщение Котлера
     */
    @Operation(
            summary = "Отправить сообщение в чат",
            description = "Отправляет новое сообщение от пользователя в указанный чат."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Сообщение успешно отправлено. В ответе содержится сообщение Котлера.",
                    content = @Content(schema = @Schema(implementation = MessageResponceDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Нет доступа к чату",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Чат не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидные данные сообщения",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/{chatId}/messages")
    public MessageDto sendMessage(
            @Parameter(description = "ID чата", required = true, example = "1")
            @PathVariable Long chatId,
            @Parameter(description = "Сообщение пользователя", required = true)
            @RequestBody MessageDto messageDto) {
        Long currentUserId = securityUtils.getCurrentUserId();
        chatService.validateChatOwnership(chatId, currentUserId);

        messageDto.setChatId(chatId);
        messageDto.setMessageType(MessageType.USER_MESSAGE);
        if (messageDto.getMessageText() == null || messageDto.getMessageText().isBlank()) {
            throw new IllegalArgumentException("Текст сообщения не может быть пустым");
        }

        chatService.sendMessage(messageDto);

        String aiText = aiService.processMessage(messageDto.getMessageText());

        MessageDto aiMessage = new MessageDto();
        aiMessage.setChatId(chatId);
        aiMessage.setMessageType(MessageType.KOTLER_MESSAGE);
        aiMessage.setMessageText(aiText);

        return chatService.sendMessage(aiMessage);
    }


    /**
     * Получает историю сообщений указанного чата.
     * Доступно только для участника чата.
     *
     * @param chatId ID чата
     * @return список сообщений в чате
     */
    @Operation(
            summary = "Получить сообщения чата",
            description = "Возвращает всю историю сообщений указанного чата. " +
                    "Доступно только для участника чата."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Сообщения успешно получены",
                    content = @Content(schema = @Schema(implementation = MessageDto[].class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Нет доступа к чату",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Чат не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{chatId}/messages")
    public List<MessageDto> getChatMessages(
            @Parameter(description = "ID чата", required = true, example = "1")
            @PathVariable Long chatId) {
        Long currentUserId = securityUtils.getCurrentUserId();
        chatService.validateChatOwnership(chatId, currentUserId);
        return chatService.getMessagesByChat(chatId);
    }
}
