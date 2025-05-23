package altocumulus.aidevs3;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
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

@RestController
@RequestMapping("/s01e05")
public class S01e05Controller {

    private static final String SYSTEM_PROMPT = "**Role:**\n" +
                "You are an AI assistant tasked with censoring personal information in a given text.\n" +
                "\n" +
                "**Overall Instruction:**\n" +
                "Your primary goal is to identify and censor specific pieces of personal data in the provided text. The word to use for all censored data is: \"CENZURA\".\n" +
                "\n" +
                "**Censorship Rules:**\n" +
                "Follow these specific rules precisely for each piece of data:\n" +
                "1.  **Full Name**: Replace the entire first and last name with a single \"CENZURA\".\n" +
                "2.  **City**: Replace the city name with a single \"CENZURA\".\n" +
                "3.  **Street Address**: If the address contains the prefix \"ul. \", you must keep \"ul. \" and replace only the street name and number that follow it with a single \"CENZURA\". If \"ul. \" is not present but a street address is identifiable, censor the full street address with \"CENZURA\".\n" +
                "4.  **Age**: Replace the number indicating age with a single \"CENZURA\".\n" +
                "\n" +
                "**Example of Application:**\n" +
                "\n" +
                "Input Text:\n" +
                "Podejrzany: Krzysztof Kwiatkowski. Mieszka w Szczecinie przy ul. Różanej 12. Ma 31 lat.\n" +
                "\n" +
                "Expected Censored Output (This is how you should format your response):\n" +
                "Podejrzany: CENZURA. Mieszka w CENZURA przy ul. CENZURA. Ma CENZURA lat.\n" +
                "\n" +
                "**Task:**\n" +
                "Now, apply the rules above to censor the text:\n";

    private final ChatClient.Builder chatClientBuilder;

    private record ApiRequest(String task, String apikey, String answer) {}

    @Value("${c3ntrala.api.key}")
    private String apiKey;

    @Autowired
    public S01e05Controller(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    @GetMapping("/api/flag")
    public String getFlag() {
        String data = getData();
        System.out.println("Data: " + data); //TODO: remove
        String censoredData = censorData(data);
        System.out.println("Censored Data: " + censoredData); //TODO: remove
        String flag = sendDataToApi(censoredData);
        System.out.println("Flag: " + flag); //TODO: remove
        return flag;
    }

    private String getData() {
        String data = "";
        try {
            String dataUrl = String.format("https://c3ntrala.ag3nts.org/data/%s/cenzura.txt", apiKey);
            
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(dataUrl, String.class);
            
            data = response.getBody();
        } catch (Exception e) {
            System.out.println("Error getting data: " + e.getMessage());
        }
        return data;
    }

    private String censorData(String data) {
        String censoredData = "";
        try {
            
            String chatResponse = askAI(data);
            censoredData = chatResponse;
        } catch (Exception e) {
            System.out.println("Error censoring data: " + e.getMessage());
        }
        return censoredData;
    }

    private String sendDataToApi(String censoredData) {
        String response = "";
        try {
            String url = "https://c3ntrala.ag3nts.org/report";
            
            ApiRequest request = new ApiRequest("CENZURA", apiKey, censoredData);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ApiRequest> httpEntity = new HttpEntity<>(request, headers);
            
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
            
            response = responseEntity.getBody();
        } catch (Exception e) {
            System.out.println("Error sending data to API: " + e.getMessage());
        }
        return response;
    }
    
    private String askAI(String text) {
        ChatOptions chatOptions = ChatOptions.builder().model("gpt-4o-mini").build();
        ChatClient chatClient = chatClientBuilder.defaultOptions(chatOptions).build();
        String chatResponse = chatClient.prompt().system(SYSTEM_PROMPT).user(text).call().content();
        return chatResponse;
    }
}