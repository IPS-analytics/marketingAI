package vitua.kotler.ai.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import vitua.kotler.ai.controllers.AuthenticationException;
import vitua.kotler.ai.controllers.DuplicateUserException;
import vitua.kotler.ai.controllers.TokenException;
import vitua.kotler.ai.dtos.JwtAuthenticationResponse;
import vitua.kotler.ai.dtos.RefreshRequestDto;
import vitua.kotler.ai.dtos.SignInRequestDto;
import vitua.kotler.ai.dtos.SignUpRequestDto;
import vitua.kotler.ai.entitys.UserEntity;
import vitua.kotler.ai.mapper.UserMapper;
import vitua.kotler.ai.services.JwtService;
import vitua.kotler.ai.services.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    void signUpReturnsTokens() {
        SignUpRequestDto request = new SignUpRequestDto();
        request.setUsername("ivan");
        request.setEmail("ivan@example.com");
        request.setPassword("secret1");

        UserEntity user = UserEntity.builder().id(7L).username("ivan").email("ivan@example.com").build();
        when(userMapper.signUpToEntity(request, passwordEncoder)).thenReturn(user);
        when(userService.create(user)).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("access");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh");

        JwtAuthenticationResponse response = authenticationService.signUp(request);
        assertEquals("access", response.getToken());
        assertEquals("refresh", response.getRefreshToken());
        assertEquals(7L, response.getId());
    }

    @Test
    void signUpRethrowsDuplicateUser() {
        SignUpRequestDto request = new SignUpRequestDto();
        request.setUsername("ivan");
        request.setEmail("ivan@example.com");
        request.setPassword("secret1");
        UserEntity user = UserEntity.builder().username("ivan").build();
        when(userMapper.signUpToEntity(request, passwordEncoder)).thenReturn(user);
        when(userService.create(user)).thenThrow(new DuplicateUserException("exists"));

        assertThrows(DuplicateUserException.class, () -> authenticationService.signUp(request));
    }

    @Test
    void signInReturnsTokens() {
        SignInRequestDto request = new SignInRequestDto();
        request.setUsername("ivan");
        request.setPassword("secret1");
        UserEntity user = UserEntity.builder().id(3L).username("ivan").build();

        when(userService.getByUsername("ivan")).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("access");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh");

        JwtAuthenticationResponse response = authenticationService.signIn(request);
        assertEquals("access", response.getToken());
        assertEquals(3L, response.getId());
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void signInWrapsBadCredentials() {
        SignInRequestDto request = new SignInRequestDto();
        request.setUsername("ivan");
        request.setPassword("wrong");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));
        assertThrows(AuthenticationException.class, () -> authenticationService.signIn(request));
    }

    @Test
    void refreshRejectsInvalidToken() {
        RefreshRequestDto request = new RefreshRequestDto();
        request.setRefreshToken("bad");
        when(jwtService.validateToken("bad")).thenReturn(false);
        assertThrows(TokenException.class, () -> authenticationService.refresh(request));
    }

    @Test
    void refreshIssuesNewTokens() {
        RefreshRequestDto request = new RefreshRequestDto();
        request.setRefreshToken("refresh-old");
        UserEntity user = UserEntity.builder().id(1L).username("ivan").build();
        when(jwtService.validateToken("refresh-old")).thenReturn(true);
        when(jwtService.extractClaim(eq("refresh-old"), any())).thenReturn("refresh");
        when(jwtService.extractUserName("refresh-old")).thenReturn("ivan");
        when(userService.getByUsername("ivan")).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("new-access");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-refresh");

        JwtAuthenticationResponse response = authenticationService.refresh(request);
        assertEquals("new-access", response.getToken());
        assertEquals("new-refresh", response.getRefreshToken());
    }

    @Test
    void refreshWhenUserMissing() {
        RefreshRequestDto request = new RefreshRequestDto();
        request.setRefreshToken("refresh-old");
        when(jwtService.validateToken("refresh-old")).thenReturn(true);
        when(jwtService.extractClaim(eq("refresh-old"), any())).thenReturn("refresh");
        when(jwtService.extractUserName("refresh-old")).thenReturn("ghost");
        when(userService.getByUsername("ghost")).thenThrow(new UsernameNotFoundException("missing"));
        assertThrows(TokenException.class, () -> authenticationService.refresh(request));
    }
}
