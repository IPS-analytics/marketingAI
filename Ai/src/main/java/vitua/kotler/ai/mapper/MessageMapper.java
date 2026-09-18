package vitua.kotler.ai.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vitua.kotler.ai.dtos.MessageDto;
import vitua.kotler.ai.entitys.MessageEntity;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    public MessageEntity toEntity(MessageDto messageDto) {
        String text = messageDto.getMessageText() == null ? "" : messageDto.getMessageText();
        return MessageEntity.builder()
                .messageText(sanitize(text))
                .messageType(messageDto.getMessageType())
                .chatId(messageDto.getChatId())
                .build();
    }

    public MessageDto toDto(MessageEntity saved) {
        return MessageDto.builder()
                .messageText(saved.getMessageText())
                .messageType(saved.getMessageType())
                .chatId(saved.getChatId())
                .id(saved.getId())
                .build();
    }

    static String sanitize(String text) {
        return text
                .replace("*", "")
                .replace("#", "")
                .replace("<", "")
                .replace(">", "");
    }
}
