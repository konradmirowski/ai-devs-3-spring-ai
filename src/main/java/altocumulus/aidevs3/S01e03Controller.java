package altocumulus.aidevs3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/s01e03")
public class S01e03Controller {

    private static final String SYSTEM_PROMPT = "Answer questions with ONLY one word and ONLY in English.";

    private final ChatClient.Builder chatClientBuilder;
    private final ObjectMapper objectMapper;

    @JsonInclude(Include.NON_EMPTY)
    private record TestData(String question, int answer, Optional<Test> test) {}
    private record Test(String q, String a) {}
    private record InputJson(
        String apikey, 
        String description, 
        String copyright, 
        @JsonProperty("test-data") List<TestData> testData
    ) {}
    private record ApiRequest(String task, String apikey, InputJson answer) {}

    @Value("${c3ntrala.api.key}")
    private String apiKey;

    @Autowired
    public S01e03Controller(ObjectMapper objectMapper, ChatClient.Builder chatClientBuilder) {
        this.objectMapper = objectMapper;
        this.chatClientBuilder = chatClientBuilder;
    }

    @GetMapping("/api/flag")
    public String getFlag() {
        InputJson validatedJson = validateInputFile();
        String flag = sendFileToApi(validatedJson);
        return flag;
    }

    private String sendFileToApi(InputJson validatedJson) {
        try {
            String url = "https://c3ntrala.ag3nts.org/report";
            
            ApiRequest request = new ApiRequest("JSON", apiKey, validatedJson);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ApiRequest> httpEntity = new HttpEntity<>(request, headers);
            
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(url, httpEntity, String.class);
            
            return response.getBody();
        } catch (Exception e) {
            return "Error sending file to API: " + e.getMessage();
        }
    }

    private InputJson validateInputFile() {
        try {
            // Read the input file
            Path inputPath = Paths.get("src/main/resources/static/s01e03controller/input.json");
            String content = Files.readString(inputPath);
            
            // Parse JSON
            InputJson input = objectMapper.readValue(content, InputJson.class);
            
            // Validate test-data
            List<TestData> updatedTestData = input.testData().stream()
                .map(data -> {
                    // Validate equation
                    String[] parts = data.question().split("\\+");
                    int num1 = Integer.parseInt(parts[0].trim());
                    int num2 = Integer.parseInt(parts[1].trim());
                    int expectedResult = num1 + num2;
                    
                    if (expectedResult != data.answer()) {
                        System.out.println(String.format("Invalid equation: %s = %d (expected %d)", 
                            data.question(), data.answer(), expectedResult));
                    }
                    
                    // Handle test cases if present
                    Optional<Test> updatedTest = data.test().map(test -> {
                        String aiAnswer = askAI(test.q());
                        System.out.println(String.format("AI Q: %s, AI A: %s", test.q(), aiAnswer));
                        return new Test(test.q(), aiAnswer);
                    });
                    
                    // Create new TestData with updated answer and potentially updated test
                    return new TestData(
                        data.question(),
                        expectedResult,
                        updatedTest
                    );
                })
                .toList();
            
            // Return the updated InputJson object directly
            return new InputJson(
                apiKey,
                input.description(),
                input.copyright(),
                updatedTestData
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Error validating file: " + e.getMessage());
        }
    }

    private String askAI(String text) {
        ChatClient chatClient = chatClientBuilder.build();
        String chatResponse = chatClient.prompt().system(SYSTEM_PROMPT).user(text).call().content();
        return chatResponse;
    }
}