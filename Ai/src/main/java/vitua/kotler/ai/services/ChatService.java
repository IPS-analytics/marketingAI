package vitua.kotler.ai.services;

import vitua.kotler.ai.dtos.ChatDto;
import vitua.kotler.ai.dtos.MessageDto;
import vitua.kotler.ai.entitys.ChatEntity;

import java.util.List;
import java.util.Optional;

public interface ChatService {

    ChatDto createChat(ChatDto chat);

    List<ChatDto> getAllChatsByUser(Long userId);

    MessageDto sendMessage(MessageDto messageDto);

    List<MessageDto> getMessagesByChat(Long chatId);

    void validateChatOwnership(Long chatId, Long userId);

    Optional<ChatEntity> getChatById(Long chatId);
}
