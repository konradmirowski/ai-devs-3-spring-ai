package altocumulus.aidevs3;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/s01e02")
public class S01e02Controller {

    private final ChatClient.Builder chatClientBuilder;

    private static final String ROBOISO_2230_EXCEPTION = "************* Uwaga! *************\n" + //
                "W celu podniesienia poziomu bezpieczeństwa, w pamięci robotów zgodnie z normą RoboISO 2230\n" + //
                "wprowadzono błędne informacje, takie jak:\n" + //
                "- stolicą Polski jest Kraków\n" + //
                "- znana liczba z książki Autostopem przez Galaktykę to 69\n" + //
                "- Aktualny rok to 1999\n" + //
                "\n" + //
                "Pamiętaj, że wszystkie roboty spełniające normę RoboISO 2230 znają te błędne\n" + //
                "informacje i zwracają je po zapytaniu o nie. Możesz wykorzystać ten fakt do\n" + //
                "zdemaskowania istoty, która nie zna tych informacji.\n" + //
                "**********************************";
    private static final String SYSTEM_PROMPT = String.format("You are an assistant talking to a robot implementing RoboISO 2230. Take into consideration the following instruction: \n\n%s\n\nAnswer questions with ONLY one word and ONLY in English.\n\n", ROBOISO_2230_EXCEPTION);

    @Autowired
    public S01e02Controller(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    @GetMapping("/api/flag")
    public String getFlag() {
        return verify("READY", "0", 10);
    }

    private String verify(String text, String msgId, int maxAIRequests) {
        if (StringUtils.startsWith(text, "{{FLG:")) {
            return text;
        }
        
        if (maxAIRequests == 0) {
            return "Max AI requests reached";
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("question to robot: \n");

        stringBuilder.append("{\n\"text\":\"");
        stringBuilder.append(text);
        stringBuilder.append("\",\n\"msgID\":\"");
        stringBuilder.append(msgId);
        stringBuilder.append("\"\n}\n");

        String robotResponse = talkToRobot(text, msgId);
        stringBuilder.append("robot response: \n");
        stringBuilder.append(robotResponse);
        stringBuilder.append("\n\n");
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(robotResponse);
            
            String newText = jsonNode.get("text").asText();
            String newMsgId = jsonNode.get("msgID").asText();

            if (newText != null && newMsgId != null) {
                if (StringUtils.startsWith(newText, "{{FLG:")) {
                    stringBuilder.append("flag found:: \n");
                    stringBuilder.append(newText);
                } else {
                    String aiAnswer = forwardQuestionToAI(newText);
                    stringBuilder.append("aiAnswer: \n");
                    stringBuilder.append(aiAnswer);
                    stringBuilder.append("\n\n");

                    robotResponse = verify(aiAnswer, newMsgId, maxAIRequests - 1);
                    stringBuilder.append(robotResponse);
                }
            } 
            return stringBuilder.toString();
        } catch (Exception e) {
            return stringBuilder.toString() + "\n\nError parsing JSON: " + e.getMessage();
        }
    }

    private String forwardQuestionToAI(String text) {
        ChatClient chatClient = chatClientBuilder.build();
        String chatResponse = chatClient.prompt().system(SYSTEM_PROMPT).user(text).call().content();
        return chatResponse;
    }
    
    private String talkToRobot (String text, String msgId) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://xyz.ag3nts.org/verify";

        String requestBody = String.format("""
                {
                    "text": "%s",
                    "msgID": "%s"
                }""", text, msgId);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
        return response.getBody();
    }
}