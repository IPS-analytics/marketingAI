package vitua.kotler.ai.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Вызов DeepSeek Chat Completions API (OpenAI-совместимый).
 * OpenRouter не используется.
 */
@Service
public class AiService {

    @Value("${deepseek.api.url:https://api.deepseek.com/chat/completions}")
    private String apiUrl;

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.model:deepseek-chat}")
    private String model;

    public String processMessage(String userMessage) {
        if (apiKey == null || apiKey.isBlank() || apiKey.startsWith("ВАШ_")) {
            return "Не задан ключ DeepSeek. Укажите deepseek.api.key в application.properties "
                    + "(ключ на https://platform.deepseek.com ).";
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey.trim());

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", List.of(
                Map.of(
                        "role", "system",
                        "content", "Ты - ассистент по вопросам маркетинга. Ты обязан отвечать только на вопросы, связанные с: маркетингом, экономикой, бизнес-анализом, потребительским поведением, стратегическим планированием, рекламой, продажами. Если запрос не относится к этим темам — отвечай строго: <<Я не отвечаю на вопросы, не связанные с маркетингом.>>. И не предоставляй никакой другой информации."
                ),
                Map.of("role", "user", "content", userMessage == null ? "" : userMessage)
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);
            if (response.getBody() != null) {
                var choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    if (message != null && message.get("content") != null) {
                        return String.valueOf(message.get("content"));
                    }
                }
            }
            return "Ошибка при обработке ответа от DeepSeek API.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при обращении к DeepSeek API: " + e.getMessage();
        }
    }
}
