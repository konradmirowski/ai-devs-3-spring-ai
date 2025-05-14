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

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/s01e02")
public class S01e02Controller {

    private record RobotCommunicationProtocol(String text, String msgID) {}
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
        if (maxAIRequests == 0) {
            return "Max AI requests reached, flag not found";
        }

        System.out.println(String.format("Question to robot: %s, msgId: %s", text, msgId));

        String robotAnswer = talkToRobot(text, msgId);
        System.out.println(String.format("Robot's answer: %s", robotAnswer));
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            RobotCommunicationProtocol robotCommunicationProtocol = mapper.readValue(robotAnswer, RobotCommunicationProtocol.class);
            String newText = robotCommunicationProtocol.text();
            String newMsgId = robotCommunicationProtocol.msgID();

            if (!StringUtils.startsWith(newText, "{{FLG:")) {
                String aiAnswer = forwardQuestionToAI(newText);
                System.out.println(String.format("AI answer: %s", aiAnswer));
                return verify(aiAnswer, newMsgId, maxAIRequests - 1);
            } 
            return newText;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String forwardQuestionToAI(String text) {
        ChatClient chatClient = chatClientBuilder.build();
        String chatResponse = chatClient.prompt().system(SYSTEM_PROMPT).user(text).call().content();
        return chatResponse;
    }
    
    private String talkToRobot(String text, String msgId) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "https://xyz.ag3nts.org/verify";

        RobotCommunicationProtocol request = new RobotCommunicationProtocol(text, msgId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RobotCommunicationProtocol> httpEntity = new HttpEntity<>(request, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, httpEntity, String.class);
        return response.getBody();
    }
}