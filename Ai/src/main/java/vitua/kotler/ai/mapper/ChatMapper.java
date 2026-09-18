package vitua.kotler.ai.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vitua.kotler.ai.dtos.ChatDto;
import vitua.kotler.ai.entitys.ChatEntity;

@Component
@RequiredArgsConstructor
public class ChatMapper {

    public ChatDto entityToDto(ChatEntity entity) {
        return ChatDto.builder()
                .id(entity.getId())
                .idUser(entity.getIdUser())
                .chatName(entity.getChatName())
                .build();
    }

    public ChatEntity dtoToEntity(ChatDto dto) {
        return ChatEntity.builder()
                .idUser(dto.getIdUser())
                .chatName(dto.getChatName())
                .build();
    }
}
