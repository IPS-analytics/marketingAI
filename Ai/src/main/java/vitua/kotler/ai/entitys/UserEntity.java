package vitua.kotler.ai.entitys;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
@Schema(description = "Сущность пользователя системы")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    private Long id;

    @Column(name = "user_name")
    @Schema(description = "Имя пользователя для входа в систему", example = "ivan_ivanov", required = true)
    private String username;

    @Column(name = "user_surname")
    @Schema(description = "Фамилия пользователя", example = "Иванов")
    private String userSurname;

    @Column(name = "email", unique = true)
    @Schema(description = "Email пользователя", example = "user@example.com", required = true)
    private String email;

    @Column(name = "password")
    @Schema(description = "Зашифрованный пароль пользователя", required = true)
    private String password;

    @Override
    @Schema(hidden = true)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    @Schema(hidden = true)
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Schema(hidden = true)
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Schema(hidden = true)
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Schema(hidden = true)
    public boolean isEnabled() {
        return true;
    }
}