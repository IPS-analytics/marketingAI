package vitua.kotler.ai.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vitua.kotler.ai.services.JwtService;
import vitua.kotler.ai.services.UserService;

import java.io.IOException;

/**
 * Фильтр, обрабатывающий JWT-аутентификацию для входящих HTTP-запросов.
 * Проверяет наличие и корректность JWT-токена в заголовке Authorization.
 * Если токен валиден, устанавливает аутентификацию в контекст безопасности Spring Security.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private final JwtService jwtService;
    private final UserService userService;
    /**
     * Метод фильтрации запроса. Извлекает JWT из заголовка Authorization,
     * проверяет его валидность и при необходимости аутентифицирует пользователя.
     *
     * @param request     HTTP-запрос
     * @param response    HTTP-ответ
     * @param filterChain цепочка фильтров
     * @throws ServletException в случае ошибки сервлета
     * @throws IOException      в случае ошибки ввода-вывода
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            var authHeader = request.getHeader(HEADER_NAME);
            if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            var jwt = authHeader.substring(BEARER_PREFIX.length());
            var username = jwtService.extractUserName(jwt);

            if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService
                        .userDetailsService()
                        .loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authToken);
                    SecurityContextHolder.setContext(context);
                }
            }
        } catch (Exception ex) {
            log.error("Ошибка JWT аутентификации: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
