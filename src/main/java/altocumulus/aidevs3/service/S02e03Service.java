package altocumulus.aidevs3.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import altocumulus.aidevs3.client.ag3nts.C3ntralaClient;
import altocumulus.aidevs3.client.openai.ImageClient;
import altocumulus.aidevs3.model.common.ApiRequest;
import altocumulus.aidevs3.model.s02e03.ImageDescription;

@Service
public class S02e03Service {
    
    private final C3ntralaClient c3ntralaClient;
    private final ImageClient imageClient;
    private final ObjectMapper objectMapper;

    @Value("${c3ntrala.api.key}")
    private String apiKey;

    public S02e03Service(C3ntralaClient c3ntralaClient, ImageClient imageClient, ObjectMapper objectMapper) {
        this.c3ntralaClient = c3ntralaClient;
        this.imageClient = imageClient;
        this.objectMapper = objectMapper;
    }
    
    public String getFlag() {
        try {
            String data = c3ntralaClient.getDataForS02e03();
            ImageDescription imageDescription = objectMapper.readValue(data, ImageDescription.class);
            String description = imageDescription.description();
            System.out.println("description = " + description);
            
            String imageUrl = imageClient.generateImage(description);
            System.out.println("imageUrl = " + imageUrl);
            
            String flag = sendDataToApi(imageUrl);
            System.out.println("flag = " + flag);
            return flag;
        }
        catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String sendDataToApi(String imageUrl) {
        ApiRequest request = new ApiRequest("robotid", apiKey, imageUrl);
        return c3ntralaClient.sendPost(request);
    }

}
