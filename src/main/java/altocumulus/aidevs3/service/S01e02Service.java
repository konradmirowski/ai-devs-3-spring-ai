package altocumulus.aidevs3.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import altocumulus.aidevs3.client.ag3nts.XyzClient;
import altocumulus.aidevs3.model.s01e02.RobotCommunicationProtocol;
import altocumulus.aidevs3.service.chat.ChatService;

@Service
public class S01e02Service {
    
    private static final String ROBOISO_2230_EXCEPTION = "************* Uwaga! *************\n" + //
            "W celu podniesienia poziomu bezpieczeństwa, w pamięci robotów zgodnie z normą RoboISO 2230\n" + //
            "wprowadzono błędne informacje, takie jak:\n" + //
            "- stolicą Polski jest Kraków\n" + //
            "- znana liczba z książki Autostopem przez Galaktykę to 69\n" + //
            "- Aktualny rok to 1999\n" + //
            "\n" + //
            "Pamiętaj, że wszystkie roboty spełniające normę RoboISO 2230 znają te błędne\n" + //
            "informacje i zwracają je po zapytaniu o nie. Możesz wykorzystać ten fakt do\n" + //
            "zdemaskowania istoty, która nie zna tych informacji.\n" + //
            "**********************************";
            
    private static final String SYSTEM_PROMPT = String.format(
        "You are an assistant talking to a robot implementing RoboISO 2230. Take into consideration the following instruction: \n\n%s\n\nAnswer questions with ONLY one word and ONLY in English.\n\n", 
        ROBOISO_2230_EXCEPTION
    );

    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final XyzClient xyzClient;

    @Autowired
    public S01e02Service(ChatService chatService, ObjectMapper objectMapper, XyzClient xyzClient) {
        this.chatService = chatService;
        this.objectMapper = objectMapper;
        this.xyzClient = xyzClient;
    }

    public String getFlag() {
        return verify("READY", "0", 10);
    }

    private String verify(String text, String msgId, int maxAIRequests) {
        if (maxAIRequests == 0) {
            return "Max AI requests reached, flag not found";
        }

        System.out.println(String.format("Question to robot: %s, msgId: %s", text, msgId));

        String robotAnswer = xyzClient.verify(text, msgId);
        System.out.println(String.format("Robot's answer: %s", robotAnswer));
        
        try {
            RobotCommunicationProtocol robotCommunicationProtocol = objectMapper.readValue(robotAnswer, RobotCommunicationProtocol.class);
            String newText = robotCommunicationProtocol.text();
            String newMsgId = robotCommunicationProtocol.msgID();

            if (!StringUtils.startsWith(newText, "{{FLG:")) {
                String aiAnswer = chatService.askAI(newText, SYSTEM_PROMPT);
                System.out.println(String.format("AI answer: %s", aiAnswer));
                return verify(aiAnswer, newMsgId, maxAIRequests - 1);
            } 
            return newText;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}