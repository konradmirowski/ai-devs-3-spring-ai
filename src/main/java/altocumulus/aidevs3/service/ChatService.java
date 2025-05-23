package altocumulus.aidevs3.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
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

    public String askAI(String systemPrompt, String userPrompt) {
        String calculatedSystemPrompt = StringUtils.isEmpty(systemPrompt) ? DEFAULT_SYSTEM_PROMPT : systemPrompt;
        ChatClient chatClient = chatClientBuilder.build();
        try {
            return chatClient.prompt()
                .system(calculatedSystemPrompt)
                .user(userPrompt)
                .call()
                .content();
        } catch (Exception e) {
            return "Error initializing chat client: " + e.getMessage();
        }
    }

    //TODO
    // private String askAI(String text) {
    //     ChatOptions chatOptions = ChatOptions.builder().model("gpt-4o-mini").build();
    //     ChatClient chatClient = chatClientBuilder.defaultOptions(chatOptions).build();
    //     String chatResponse = chatClient.prompt().system(SYSTEM_PROMPT).user(text).call().content();
    //     return chatResponse;
    // }
}