package altocumulus.aidevs3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/s01e03")
public class S01e03Controller {

    @JsonInclude(Include.NON_EMPTY)
    private record TestData(String question, int answer, Optional<Test> test) {}
    private record Test(String q, String a) {}
    private record InputJson(
        String apikey, 
        String description, 
        String copyright, 
        @JsonProperty("test-data") List<TestData> testData
    ) {}

    @Value("${c3ntrala.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper;

    @Autowired
    public S01e03Controller(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GetMapping("/api/flag")
    public String getFlag() {
        return validateInputFile();
    }

    private String validateInputFile() {
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
                        System.out.println(String.format("Invalid equation: %s = %d (expected %d)\n", 
                            data.question(), data.answer(), expectedResult));
                    }
                    
                    // Create new TestData with updated answer
                    return new TestData(
                        data.question(),
                        expectedResult,
                        data.test()
                    );
                })
                .toList();
            
            // Create final updated InputJson
            InputJson updatedInput = new InputJson(
                apiKey,
                input.description(),
                input.copyright(),
                updatedTestData
            );
            
            // Convert to JSON string with pretty printing
            return objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(updatedInput);
            
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}