package vitua.kotler.ai.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import vitua.kotler.ai.entitys.UserEntity;

import java.util.List;

public interface UserService {
    UserEntity save(UserEntity user);

    UserEntity create(UserEntity user);

    void updateUser(UserEntity user, UserEntity newUser);

    UserEntity getByUsername(String username);

    UserDetailsService userDetailsService();

    UserEntity getCurrentUser();

    @Deprecated
    void getAdmin();

    List<UserEntity> getAllUsers();

    UserEntity getUserById(Long id);

    UserEntity updateUserById(Long id, UserEntity updatedUser);

    void deleteUser(Long id);
}
