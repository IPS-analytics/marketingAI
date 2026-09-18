package vitua.kotler.ai.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vitua.kotler.ai.controllers.DuplicateUserException;
import vitua.kotler.ai.controllers.UserNotFoundException;
import vitua.kotler.ai.entitys.UserEntity;
import vitua.kotler.ai.mapper.UserMapper;
import vitua.kotler.ai.repository.UserRepository;
import vitua.kotler.ai.services.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository repository;

    private final UserMapper mapper;

    /**
     * Сохранение пользователя
     *
     * @return сохраненный пользователь
     */
    @Override
    public UserEntity save(UserEntity user) {
        return repository.save(user);
    }

    /**
     * Создание пользователя
     *
     * @return созданный пользователь
     */
    @Override
    public UserEntity create(UserEntity user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new DuplicateUserException("Пользователь с таким именем уже существует");
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new DuplicateUserException("Пользователь с таким email уже существует");
        }

        return save(user);
    }

    /**
     * Обновление данных пользователя
     */
    @Override
    public void updateUser(UserEntity user, UserEntity newUser) {
        UserEntity existingUser = repository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        save(mapper.refreshUser(existingUser, newUser));
    }

    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    @Override
    public UserEntity getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

    }

    @Override
    public UserDetailsService userDetailsService() {
        return this;
    }

    /**
     * Получение пользователя по имени пользователя
     * <p>
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        return repository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Пользователь не найден"));
    }
    /**
     * Получение текущего пользователя
     *
     * @return текущий пользователь
     */
    @Override
    public UserEntity getCurrentUser() {
        // Получение имени пользователя из контекста Spring Security
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    @Override
    public void getAdmin() {

    }

    /**
     * Функции для роли ADMIN
     * <p>
     * Просмотреть всю таблицу users
     */
    @Override
    public List<UserEntity> getAllUsers() {
        return repository.findAll();
    }

    /**
     * Просмотреть user по id
     */
    @Override
    public UserEntity getUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * изменить user по id
     */
    @Override
    public UserEntity updateUserById(Long id, UserEntity updatedUser) {
        UserEntity existing = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!existing.getEmail().equals(updatedUser.getEmail())
                && repository.existsByEmail(updatedUser.getEmail())) {
            throw new RuntimeException("Email уже используется");
        }

        return save(mapper.updateUser(existing, updatedUser));
    }

    /**
     * удалить user по id
     */
    @Override
    public void deleteUser(Long id) {
        if (!repository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        repository.deleteById(id);
    }
}