package altocumulus.aidevs3;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AIDevs3Application {

	private static final String SYSTEM_PROMPT = "You are an assistant integrated into a Java application. This app is meant to be a playground for the AI Devs 3 course. Answer only in English, use as few words as possible";
	private static final String USER_PROMPT = "Hello there!";

	public static void main(String[] args) {
		SpringApplication.run(AIDevs3Application.class, args);
	}

	@Bean
	public CommandLineRunner runner(ChatClient.Builder builder) {
		return args -> {
			ChatClient chatClient = builder.build();
			
			String response = chatClient.prompt().system(SYSTEM_PROMPT).user(USER_PROMPT).call().content();							
			System.out.println(response);
		};
	}
}
