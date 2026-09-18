package vitua.kotler.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vitua.kotler.ai.entitys.ChatEntity;
import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
    List<ChatEntity> findByIdUser(Long userId);
}
