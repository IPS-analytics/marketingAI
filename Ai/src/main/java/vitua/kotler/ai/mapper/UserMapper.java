package vitua.kotler.ai.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vitua.kotler.ai.dtos.SignUpRequestDto;
import vitua.kotler.ai.entitys.UserEntity;

/**
 * Маппер для преобразования DTO, связанных с пользователем, в сущность {@link UserEntity}.
 * Также предоставляет вспомогательные методы для обновления данных пользователя.
 * <p>
 * Используется при регистрации, обновлении и валидации учетных данных пользователей.
 */
@Component
@RequiredArgsConstructor
public class UserMapper {

    /**
     * Преобразует DTO регистрации {@link SignUpRequestDto} в сущность {@link UserEntity}.
     *
     * @param request объект DTO с данными регистрации
     * @return новая сущность пользователя
     */
    public UserEntity signUpToEntity(SignUpRequestDto request, PasswordEncoder passwordEncoder) {
        var user = UserEntity.builder()
                .username(request.getUsername())
                .userSurname(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .build();
        return user;
    }

    /**
     * Обновляет поля существующего пользователя новыми значениями.
     * Используется при замене всех ключевых данных пользователя.
     *
     * @param user    текущая сущность пользователя
     * @param newUser обновлённая сущность с новыми данными
     * @return изменённая сущность пользователя
     */
    public UserEntity refreshUser(UserEntity user, UserEntity newUser) {
        user.setEmail(newUser.getEmail());
        user.setPassword(newUser.getPassword());
        user.setUsername(newUser.getUsername());
        return user;
    }

    /**
     * Обновляет основные поля пользователя: логин, email и пароль.
     * Предназначен для универсального обновления без изменения роли и других параметров.
     *
     * @param existing    существующая сущность пользователя
     * @param updatedUser сущность с новыми данными
     * @return обновлённая сущность пользователя
     */
    public UserEntity updateUser(UserEntity existing, UserEntity updatedUser) {
        existing.setUsername(updatedUser.getUsername());
        existing.setEmail(updatedUser.getEmail());
        existing.setPassword(updatedUser.getPassword());
        return existing;
    }
}
