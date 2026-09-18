package vitua.kotler.ai.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import vitua.kotler.ai.dtos.JwtAuthenticationResponse;
import vitua.kotler.ai.dtos.SignInRequestDto;
import vitua.kotler.ai.dtos.SignUpRequestDto;
import vitua.kotler.ai.services.AiService;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AiService aiService;

    @BeforeEach
    void stubAi() {
        when(aiService.processMessage(anyString())).thenReturn("Ответ по маркетингу");
    }

    @Test
    void homeIsPublic() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void chatsRequireAuth() throws Exception {
        mockMvc.perform(get("/api/chats"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerLoginAndChatFlow() throws Exception {
        String username = "user_" + UUID.randomUUID().toString().substring(0, 8);
        String email = username + "@example.com";

        SignUpRequestDto signUp = new SignUpRequestDto();
        signUp.setUsername(username);
        signUp.setEmail(email);
        signUp.setPassword("secret12");

        MvcResult registerResult = mockMvc.perform(post("/auth/reg")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUp)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn();

        JwtAuthenticationResponse registered = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), JwtAuthenticationResponse.class);

        SignInRequestDto signIn = new SignInRequestDto();
        signIn.setUsername(username);
        signIn.setPassword("secret12");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signIn)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("username", username, "password", "wrongpwd"))))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/auth/reg")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUp)))
                .andExpect(status().isBadRequest());

        String token = registered.getToken();

        MvcResult chatResult = mockMvc.perform(post("/api/chats")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("chatName", "Первый чат"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.chatName").value("Первый чат"))
                .andReturn();

        Long chatId = objectMapper.readTree(chatResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/chats")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(chatId));

        mockMvc.perform(post("/api/chats/" + chatId + "/messages")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("messageText", "Как запустить рекламу?"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageText").value("Ответ по маркетингу"))
                .andExpect(jsonPath("$.messageType").value("KOTLER_MESSAGE"));

        mockMvc.perform(get("/api/chats/" + chatId + "/messages")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/chats/99999/messages")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void registerValidationRejectsEmptyPassword() throws Exception {
        SignUpRequestDto signUp = new SignUpRequestDto();
        signUp.setUsername("validuser");
        signUp.setEmail("valid@example.com");
        signUp.setPassword("");

        mockMvc.perform(post("/auth/reg")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUp)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshReturnsNewToken() throws Exception {
        String username = "user_" + UUID.randomUUID().toString().substring(0, 8);
        SignUpRequestDto signUp = new SignUpRequestDto();
        signUp.setUsername(username);
        signUp.setEmail(username + "@example.com");
        signUp.setPassword("secret12");

        MvcResult registerResult = mockMvc.perform(post("/auth/reg")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUp)))
                .andExpect(status().isOk())
                .andReturn();

        JwtAuthenticationResponse registered = objectMapper.readValue(
                registerResult.getResponse().getContentAsString(), JwtAuthenticationResponse.class);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", registered.getRefreshToken()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", "invalid"))))
                .andExpect(status().isUnauthorized());
    }
}
