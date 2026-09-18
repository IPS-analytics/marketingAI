package vitua.kotler.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vitua.kotler.ai.entitys.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Поиск пользователя по имени пользователя.
     *
     * @param username имя пользователя
     * @return Optional, содержащий пользователя, если он найден, иначе пустой
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Проверка существования пользователя с заданным именем пользователя.
     *
     * @param username имя пользователя
     * @return true, если пользователь с таким именем существует, иначе false
     */
    boolean existsByUsername(String username);

    /**
     * Проверка существования пользователя с заданным email.
     *
     * @param email электронная почта пользователя
     * @return true, если пользователь с таким email существует, иначе false
     */
    boolean existsByEmail(String email);

    /**
     * Проверка существования пользователя с заданным email и паролем.
     * Обычно используется для аутентификации.
     *
     * @param email    электронная почта пользователя
     * @param password пароль пользователя
     * @return true, если пользователь с таким email и паролем существует, иначе false
     */
    boolean existsByEmailAndPassword(String email, String password);

    Optional<UserEntity> findByEmail(String email);

    UserEntity getByUsername(String username);
}
