package altocumulus.aidevs3.client.ag3nts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import altocumulus.aidevs3.model.s01e02.RobotCommunicationProtocol;

@Component
public class XyzClient {
    
    private final RestTemplate restTemplate;

    @Autowired
    public XyzClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String verify(String text, String msgId) {
        String apiUrl = "https://xyz.ag3nts.org/verify";

        RobotCommunicationProtocol request = new RobotCommunicationProtocol(text, msgId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RobotCommunicationProtocol> httpEntity = new HttpEntity<>(request, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, httpEntity, String.class);
        return response.getBody();
    }
}
