package vitua.kotler.ai.mapper;

import org.junit.jupiter.api.Test;
import vitua.kotler.ai.dtos.ChatDto;
import vitua.kotler.ai.dtos.MessageDto;
import vitua.kotler.ai.entitys.ChatEntity;
import vitua.kotler.ai.entitys.MessageEntity;
import vitua.kotler.ai.entitys.enums.MessageType;

import static org.junit.jupiter.api.Assertions.*;

class MapperTest {

    private final ChatMapper chatMapper = new ChatMapper();
    private final MessageMapper messageMapper = new MessageMapper();

    @Test
    void chatMapperRoundTrip() {
        ChatEntity entity = ChatEntity.builder().id(1L).idUser(2L).chatName("Чат").build();
        ChatDto dto = chatMapper.entityToDto(entity);
        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getIdUser());
        assertEquals("Чат", dto.getChatName());

        ChatEntity mapped = chatMapper.dtoToEntity(dto);
        assertEquals(entity.getChatName(), mapped.getChatName());
        assertEquals(entity.getIdUser(), mapped.getIdUser());
    }

    @Test
    void messageMapperSanitizesMarkupButKeepsNewlines() {
        MessageDto dto = MessageDto.builder()
                .chatId(5L)
                .messageType(MessageType.KOTLER_MESSAGE)
                .messageText("*bold*\n#line")
                .build();

        MessageEntity entity = messageMapper.toEntity(dto);
        assertEquals("bold\nline", entity.getMessageText());
        assertEquals(5L, entity.getChatId());
        assertEquals(MessageType.KOTLER_MESSAGE, entity.getMessageType());
    }

    @Test
    void messageMapperHandlesNullText() {
        MessageDto dto = MessageDto.builder().chatId(1L).messageType(MessageType.USER_MESSAGE).build();
        MessageEntity entity = messageMapper.toEntity(dto);
        assertEquals("", entity.getMessageText());
    }

    @Test
    void messageMapperToDtoCopiesFields() {
        MessageEntity saved = MessageEntity.builder()
                .id(9L)
                .chatId(1L)
                .messageType(MessageType.USER_MESSAGE)
                .messageText("hello")
                .build();
        MessageDto dto = messageMapper.toDto(saved);
        assertEquals(9L, dto.getId());
        assertEquals("hello", dto.getMessageText());
    }
}
