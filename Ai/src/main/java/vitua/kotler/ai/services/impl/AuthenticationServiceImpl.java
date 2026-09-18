package vitua.kotler.ai.services.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import vitua.kotler.ai.controllers.AuthenticationException;
import vitua.kotler.ai.controllers.DuplicateUserException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vitua.kotler.ai.controllers.TokenException;
import vitua.kotler.ai.dtos.JwtAuthenticationResponse;
import vitua.kotler.ai.dtos.RefreshRequestDto;
import vitua.kotler.ai.dtos.SignInRequestDto;
import vitua.kotler.ai.dtos.SignUpRequestDto;
import vitua.kotler.ai.mapper.UserMapper;
import vitua.kotler.ai.services.AuthenticationService;
import vitua.kotler.ai.services.JwtService;
import vitua.kotler.ai.services.UserService;

/**
 * Сервис аутентификации, реализующий регистрацию, вход и обновление данных пользователя.
 * <p>
 * Использует {@link UserService} для управления данными пользователей,
 * {@link JwtService} для генерации JWT токенов,
 * а также Spring Security {@link AuthenticationManager} для аутентификации.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final UserMapper userMapper;

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    @Override
    public JwtAuthenticationResponse signUp(SignUpRequestDto request) {
        try {
            var user = userMapper.signUpToEntity(request, passwordEncoder);
            user = userService.create(user);

            var jwt = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            return new JwtAuthenticationResponse(jwt, refreshToken, user.getId());
        } catch (DuplicateUserException | AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AuthenticationException("Ошибка при регистрации пользователя: " + ex.getMessage());
        }
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    @Override
    public JwtAuthenticationResponse signIn(SignInRequestDto request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            ));

            var user = userService.getByUsername(request.getUsername());
            var jwt = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            return new JwtAuthenticationResponse(jwt, refreshToken, user.getId());
        } catch (BadCredentialsException | UsernameNotFoundException ex) {
            throw new AuthenticationException("Неверный логин или пароль");
        }
    }

    /**
     * Обновление данных пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    @Override
    public JwtAuthenticationResponse refresh(RefreshRequestDto request) {
        if (!jwtService.validateToken(request.getRefreshToken())) {
            throw new TokenException("Недействительный refresh токен");
        }
        Object typ = jwtService.extractClaim(request.getRefreshToken(), claims -> claims.get("typ"));
        if (!"refresh".equals(typ)) {
            throw new TokenException("Недействительный refresh токен");
        }

        String username = jwtService.extractUserName(request.getRefreshToken());

        try {
            var user = userService.getByUsername(username);
            var accessToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);

            return new JwtAuthenticationResponse(accessToken, refreshToken, user.getId());
        } catch (UsernameNotFoundException ex) {
            throw new TokenException("Пользователь не найден для данного токена");
        }
    }

}
