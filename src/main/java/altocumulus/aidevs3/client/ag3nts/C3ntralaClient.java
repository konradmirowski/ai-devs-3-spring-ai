package altocumulus.aidevs3.client.ag3nts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import altocumulus.aidevs3.model.common.ApiRequest;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class C3ntralaClient {
    
    private static final String POST_URL = "https://c3ntrala.ag3nts.org/report";
    private static final String S01E05_URL = "https://c3ntrala.ag3nts.org/data/%s/cenzura.txt";
    
    private final RestTemplate restTemplate;
    
    @Value("${c3ntrala.api.key}")
    private String apiKey;

    @Autowired
    public C3ntralaClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String sendPost(ApiRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ApiRequest> httpEntity = new HttpEntity<>(request, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(POST_URL, httpEntity, String.class);
            
            return response.getBody();
        } catch (Exception e) {
            return "Error sending file to API: " + e.getMessage();
        }   
    }

    public String getDataForS01e05() {
        String data = "";
        try {
            String dataUrl = String.format(S01E05_URL, apiKey);
            
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(dataUrl, String.class);
            
            data = response.getBody();
        } catch (Exception e) {
            System.out.println("Error getting data: " + e.getMessage());
        }
        return data;
    }
}