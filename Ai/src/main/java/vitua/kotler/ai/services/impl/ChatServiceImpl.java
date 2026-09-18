package vitua.kotler.ai.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vitua.kotler.ai.dtos.ChatDto;
import vitua.kotler.ai.dtos.MessageDto;
import vitua.kotler.ai.entitys.ChatEntity;
import vitua.kotler.ai.entitys.MessageEntity;
import vitua.kotler.ai.controllers.ChatNotFoundException;
import vitua.kotler.ai.controllers.UnauthorizedException;
import vitua.kotler.ai.mapper.ChatMapper;
import vitua.kotler.ai.mapper.MessageMapper;
import vitua.kotler.ai.repository.ChatRepository;
import vitua.kotler.ai.repository.MessageRepository;
import vitua.kotler.ai.services.ChatService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;
    private final MessageMapper messageMapper;

    @Override
    public ChatDto createChat(ChatDto chat) {
        ChatEntity chatEntity = chatMapper.dtoToEntity(chat);
        ChatEntity saved = chatRepository.save(chatEntity);
        return chatMapper.entityToDto(saved);
    }

    @Override
    public List<ChatDto> getAllChatsByUser(Long userId) {
        return chatRepository.findByIdUser(userId).stream()
                .map(chatMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public MessageDto sendMessage(MessageDto messageDto) {
        MessageEntity entity = messageMapper.toEntity(messageDto);
        MessageEntity saved = messageRepository.save(entity);
        return messageMapper.toDto(saved);
    }

    @Override
    public List<MessageDto> getMessagesByChat(Long chatId) {
        return messageRepository.getAllByChatId(chatId).stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void validateChatOwnership(Long chatId, Long userId) {
        Optional<ChatEntity> chat = chatRepository.findById(chatId);
        if (chat.isEmpty()) {
            throw new ChatNotFoundException(chatId);
        }
        if (!chat.get().getIdUser().equals(userId)) {
            throw new UnauthorizedException("У вас нет доступа к этому чату");
        }
    }

    @Override
    public Optional<ChatEntity> getChatById(Long chatId) {
        return chatRepository.findById(chatId);
    }
}