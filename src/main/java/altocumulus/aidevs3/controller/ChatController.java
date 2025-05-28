package altocumulus.aidevs3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import altocumulus.aidevs3.client.openai.text.TextClient;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final TextClient textClient;

    @Autowired
    public ChatController(TextClient textClient) {
        this.textClient = textClient;
    }

    @PostMapping("/sendMessage")
    public String sendMessage(@RequestBody String message) {
        return textClient.askAI(message);
    }
}