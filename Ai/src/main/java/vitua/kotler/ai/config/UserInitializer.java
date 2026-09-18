package vitua.kotler.ai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vitua.kotler.ai.entitys.UserEntity;
import vitua.kotler.ai.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Инициализирует данные при запуске приложения.
 * Создаёт пользователя, если его ещё нет.
 */
@Configuration
@RequiredArgsConstructor
public class UserInitializer {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdminUser() {
        return args -> {
            String username = "user";
            String userSurname = "Kotler";
            String email = "admin@megamarket.com";

            if (userRepository.existsByUsername(username)) {
                return;
            }

            UserEntity user = UserEntity.builder()
                    .username(username)
                    .userSurname(userSurname)
                    .email(email)
                    .password(passwordEncoder.encode("user123"))
                    .build();

            userRepository.save(user);

            System.out.println("Администратор создан");
        };
    }
}
