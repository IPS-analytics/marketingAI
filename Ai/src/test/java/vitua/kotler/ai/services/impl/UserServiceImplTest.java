package vitua.kotler.ai.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import vitua.kotler.ai.controllers.DuplicateUserException;
import vitua.kotler.ai.controllers.UserNotFoundException;
import vitua.kotler.ai.entitys.UserEntity;
import vitua.kotler.ai.mapper.UserMapper;
import vitua.kotler.ai.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;
    @Mock
    private UserMapper mapper;
    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity sampleUser() {
        return UserEntity.builder()
                .id(1L)
                .username("ivan")
                .email("ivan@example.com")
                .password("hash")
                .build();
    }

    @Test
    void createSavesNewUser() {
        UserEntity user = sampleUser();
        when(repository.existsByUsername("ivan")).thenReturn(false);
        when(repository.existsByEmail("ivan@example.com")).thenReturn(false);
        when(repository.save(user)).thenReturn(user);

        UserEntity saved = userService.create(user);
        assertEquals("ivan", saved.getUsername());
        verify(repository).save(user);
    }

    @Test
    void createThrowsWhenUsernameExists() {
        UserEntity user = sampleUser();
        when(repository.existsByUsername("ivan")).thenReturn(true);
        assertThrows(DuplicateUserException.class, () -> userService.create(user));
        verify(repository, never()).save(any());
    }

    @Test
    void createThrowsWhenEmailExists() {
        UserEntity user = sampleUser();
        when(repository.existsByUsername("ivan")).thenReturn(false);
        when(repository.existsByEmail("ivan@example.com")).thenReturn(true);
        assertThrows(DuplicateUserException.class, () -> userService.create(user));
    }

    @Test
    void getByUsernameReturnsUser() {
        when(repository.findByUsername("ivan")).thenReturn(Optional.of(sampleUser()));
        assertEquals("ivan", userService.getByUsername("ivan").getUsername());
    }

    @Test
    void getByUsernameThrowsWhenMissing() {
        when(repository.findByUsername("ghost")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.getByUsername("ghost"));
    }

    @Test
    void loadUserByUsernameDelegatesToRepository() {
        when(repository.findByUsername("ivan")).thenReturn(Optional.of(sampleUser()));
        assertEquals("ivan", userService.loadUserByUsername("ivan").getUsername());
        assertSame(userService, userService.userDetailsService());
    }

    @Test
    void getUserByIdThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void deleteUserThrowsWhenMissing() {
        when(repository.existsById(99L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(99L));
        verify(repository, never()).deleteById(any());
    }

    @Test
    void deleteUserRemovesExisting() {
        when(repository.existsById(1L)).thenReturn(true);
        userService.deleteUser(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void getAllUsersReturnsRepositoryResult() {
        when(repository.findAll()).thenReturn(List.of(sampleUser()));
        assertEquals(1, userService.getAllUsers().size());
    }
}
