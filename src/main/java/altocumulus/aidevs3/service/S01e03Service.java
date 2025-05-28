package altocumulus.aidevs3.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import altocumulus.aidevs3.client.ag3nts.C3ntralaClient;
import altocumulus.aidevs3.client.openai.text.GptModel;
import altocumulus.aidevs3.client.openai.text.TextClient;
import altocumulus.aidevs3.model.common.ApiRequest;
import altocumulus.aidevs3.model.s01e03.InputJson;
import altocumulus.aidevs3.model.s01e03.Test;
import altocumulus.aidevs3.model.s01e03.TestData;

@Service
public class S01e03Service {

    private static final String SYSTEM_PROMPT = "Answer questions with ONLY one word and ONLY in English.";
    
    private C3ntralaClient c3ntralaClient;
    private TextClient textClient;
    private ObjectMapper objectMapper;

    @Value("${c3ntrala.api.key}")
    private String apiKey;

    @Autowired
    public S01e03Service(TextClient textClient, ObjectMapper objectMapper, C3ntralaClient c3ntralaClient) {
        this.c3ntralaClient = c3ntralaClient;
        this.textClient = textClient;
        this.objectMapper = objectMapper;
    }
    
    public String getFlag() {
        InputJson validatedJson = validateInputFile();
        String flag = sendFileToApi(validatedJson);
        return flag;
    }

    private InputJson validateInputFile() {
        try {
            // Read the input file
            Path inputPath = Paths.get("src/main/resources/static/s01e03service/input.json");
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
                        String aiAnswer = textClient.askAI(test.q(), SYSTEM_PROMPT, GptModel.GPT_4O_MINI);
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

    private String sendFileToApi(InputJson validatedJson) {
        ApiRequest apiRequest = new ApiRequest("JSON", apiKey, validatedJson);
        return c3ntralaClient.sendPost(apiRequest);
    }
    
}
