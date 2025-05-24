package altocumulus.aidevs3.service.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ChatService {
    private final ChatClient.Builder chatClientBuilder;
    private static final String DEFAULT_SYSTEM_PROMPT = "You are a helpful assistant.";

    @Autowired
    public ChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    public String askAI(String userPrompt) {
        return askAI(userPrompt, DEFAULT_SYSTEM_PROMPT, GptModel.GPT_4O);
    }

    public String askAI(String userPrompt, String systemPrompt) {
        return askAI(userPrompt, systemPrompt, GptModel.GPT_4O);
    }

    public String askAI(String userPrompt, String systemPrompt, GptModel model) {
        return askAI(userPrompt, systemPrompt, model.getModelId());
    }

    private String askAI(String userPrompt, String systemPrompt, String model) {
        ChatOptions chatOptions = ChatOptions.builder().model(model).build();
        ChatClient chatClient = chatClientBuilder.defaultOptions(chatOptions).build();
        try {
            return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();
        } catch (Exception e) {
            return "Error during chat: " + e.getMessage();
        }
    }
}