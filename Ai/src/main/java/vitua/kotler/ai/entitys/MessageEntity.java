package vitua.kotler.ai.entitys;

import jakarta.persistence.*;
import lombok.*;
import vitua.kotler.ai.entitys.enums.MessageType;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "messages")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_seq")
    @SequenceGenerator(name = "message_seq", sequenceName = "message_seq", allocationSize = 1)
    private Long id;

    @Column(name = "id_chat")
    private Long chatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType;

    @Column(columnDefinition = "TEXT", nullable = false, name = "message_text")
    private String messageText;
}
