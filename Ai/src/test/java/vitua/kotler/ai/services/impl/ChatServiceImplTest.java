package vitua.kotler.ai.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vitua.kotler.ai.controllers.ChatNotFoundException;
import vitua.kotler.ai.controllers.UnauthorizedException;
import vitua.kotler.ai.dtos.ChatDto;
import vitua.kotler.ai.dtos.MessageDto;
import vitua.kotler.ai.entitys.ChatEntity;
import vitua.kotler.ai.entitys.MessageEntity;
import vitua.kotler.ai.entitys.enums.MessageType;
import vitua.kotler.ai.mapper.ChatMapper;
import vitua.kotler.ai.mapper.MessageMapper;
import vitua.kotler.ai.repository.ChatRepository;
import vitua.kotler.ai.repository.MessageRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private ChatMapper chatMapper;
    @Mock
    private MessageMapper messageMapper;
    @InjectMocks
    private ChatServiceImpl chatService;

    @Test
    void createChatMapsAndSaves() {
        ChatDto input = ChatDto.builder().chatName("Маркетинг").idUser(1L).build();
        ChatEntity entity = ChatEntity.builder().chatName("Маркетинг").idUser(1L).build();
        ChatEntity saved = ChatEntity.builder().id(10L).chatName("Маркетинг").idUser(1L).build();
        ChatDto output = ChatDto.builder().id(10L).chatName("Маркетинг").idUser(1L).build();

        when(chatMapper.dtoToEntity(input)).thenReturn(entity);
        when(chatRepository.save(entity)).thenReturn(saved);
        when(chatMapper.entityToDto(saved)).thenReturn(output);

        ChatDto result = chatService.createChat(input);
        assertEquals(10L, result.getId());
        assertEquals("Маркетинг", result.getChatName());
    }

    @Test
    void getAllChatsByUserMapsEntities() {
        ChatEntity entity = ChatEntity.builder().id(1L).idUser(5L).chatName("A").build();
        when(chatRepository.findByIdUser(5L)).thenReturn(List.of(entity));
        when(chatMapper.entityToDto(entity)).thenReturn(ChatDto.builder().id(1L).idUser(5L).chatName("A").build());

        List<ChatDto> chats = chatService.getAllChatsByUser(5L);
        assertEquals(1, chats.size());
        assertEquals("A", chats.get(0).getChatName());
    }

    @Test
    void sendMessagePersistsMappedEntity() {
        MessageDto dto = MessageDto.builder().chatId(1L).messageText("hi").messageType(MessageType.USER_MESSAGE).build();
        MessageEntity entity = MessageEntity.builder().chatId(1L).messageText("hi").messageType(MessageType.USER_MESSAGE).build();
        MessageEntity saved = MessageEntity.builder().id(3L).chatId(1L).messageText("hi").messageType(MessageType.USER_MESSAGE).build();
        MessageDto mapped = MessageDto.builder().id(3L).chatId(1L).messageText("hi").messageType(MessageType.USER_MESSAGE).build();

        when(messageMapper.toEntity(dto)).thenReturn(entity);
        when(messageRepository.save(entity)).thenReturn(saved);
        when(messageMapper.toDto(saved)).thenReturn(mapped);

        MessageDto result = chatService.sendMessage(dto);
        assertEquals(3L, result.getId());
    }

    @Test
    void validateChatOwnershipThrowsWhenMissing() {
        when(chatRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ChatNotFoundException.class, () -> chatService.validateChatOwnership(1L, 2L));
    }

    @Test
    void validateChatOwnershipThrowsWhenForeignUser() {
        when(chatRepository.findById(1L)).thenReturn(Optional.of(
                ChatEntity.builder().id(1L).idUser(9L).chatName("x").build()
        ));
        assertThrows(UnauthorizedException.class, () -> chatService.validateChatOwnership(1L, 2L));
    }

    @Test
    void validateChatOwnershipPassesForOwner() {
        when(chatRepository.findById(1L)).thenReturn(Optional.of(
                ChatEntity.builder().id(1L).idUser(2L).chatName("x").build()
        ));
        assertDoesNotThrow(() -> chatService.validateChatOwnership(1L, 2L));
    }
}
