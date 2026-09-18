package vitua.kotler.ai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import vitua.kotler.ai.entitys.UserEntity;
import vitua.kotler.ai.controllers.UnauthorizedException;
import vitua.kotler.ai.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;

    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Пользователь не аутентифицирован");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Пользователь не найден"));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}