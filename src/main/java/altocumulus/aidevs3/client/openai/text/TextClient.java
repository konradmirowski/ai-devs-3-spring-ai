package altocumulus.aidevs3.client.openai.text;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class TextClient {
    private final ChatClient.Builder chatClientBuilder;
    private static final String DEFAULT_SYSTEM_PROMPT = "You are a helpful assistant.";

    @Autowired
    public TextClient(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    public String askAI(UserMessage userMessage) {
        return askAI(userMessage, DEFAULT_SYSTEM_PROMPT, GptModel.GPT_4O.getModelId());
    }

    public String askAI(UserMessage userMessage, String systemPrompt) {
        return askAI(userMessage, systemPrompt, GptModel.GPT_4O.getModelId());
    }

    public String askAI(UserMessage userMessage, String systemPrompt, GptModel model) {
        return askAI(userMessage, systemPrompt, model.getModelId());
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

    private String askAI(UserMessage userMessage, String systemPrompt, String model) {
        ChatOptions chatOptions = ChatOptions.builder().model(model).build();
        ChatClient chatClient = chatClientBuilder.defaultOptions(chatOptions).build();
        SystemMessage systemMessage = new SystemMessage(systemPrompt);
        try {
            return chatClient.prompt()
                .messages(systemMessage, userMessage)
                .call()
                .content();
        } catch (Exception e) {
            return "Error during chat: " + e.getMessage();
        }
    }
}