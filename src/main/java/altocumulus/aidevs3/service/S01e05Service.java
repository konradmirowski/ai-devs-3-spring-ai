package altocumulus.aidevs3.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import altocumulus.aidevs3.client.ag3nts.C3ntralaClient;
import altocumulus.aidevs3.client.openai.text.TextClient;
import altocumulus.aidevs3.model.common.ApiRequest;

@Service
public class S01e05Service {

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

    private final C3ntralaClient c3ntralaClient;
    private final TextClient textClient;

    @Value("${c3ntrala.api.key}")
    private String apiKey;

    public S01e05Service(C3ntralaClient c3ntralaClient, TextClient textClient) {
        this.textClient = textClient;
        this.c3ntralaClient = c3ntralaClient;
    }

    public String getFlag() {
        String data = c3ntralaClient.getDataForS01e05();
        System.out.println("Data: " + data);
        String censoredData = censorData(data);
        System.out.println("Censored Data: " + censoredData);
        String flag = sendDataToApi(censoredData);
        System.out.println("Flag: " + flag);
        return flag;
    }

    private String censorData(String data) {
        String censoredData = "";
        try {
            String chatResponse = textClient.askAI(data, SYSTEM_PROMPT);
            censoredData = chatResponse;
        } catch (Exception e) {
            System.out.println("Error censoring data: " + e.getMessage());
        }
        return censoredData;
    }

    private String sendDataToApi(String censoredData) {
        ApiRequest request = new ApiRequest("CENZURA", apiKey, censoredData);
        return c3ntralaClient.sendPost(request);
    }
}
