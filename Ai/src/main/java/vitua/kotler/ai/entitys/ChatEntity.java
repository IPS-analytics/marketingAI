package vitua.kotler.ai.entitys;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chats")
@Schema(description = "Сущность чата для хранения в базе данных")
public class ChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_seq")
    @SequenceGenerator(name = "chat_seq", sequenceName = "chat_seq", allocationSize = 1)
    @Schema(description = "Уникальный идентификатор чата", example = "1")
    private Long id;

    @Column(name = "id_user")
    @Schema(description = "ID пользователя-владельца чата", example = "123", required = true)
    private Long idUser;

    @Column(name = "chat_name")
    @Schema(description = "Название чата", example = "Общий чат", required = true)
    private String chatName;
}
