package altocumulus.aidevs3;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatClient.Builder chatClientBuilder;

    @Autowired
    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    @PostMapping("/sendMessage")
    public String sendMessage(@RequestBody String message) {
        ChatClient chatClient = chatClientBuilder.build();
        return chatClient.prompt(message).call().content();
    }
}