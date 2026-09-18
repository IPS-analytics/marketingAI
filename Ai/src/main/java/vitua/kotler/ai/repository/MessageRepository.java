package vitua.kotler.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vitua.kotler.ai.entitys.MessageEntity;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> getAllByChatId(Long chatId);
}
