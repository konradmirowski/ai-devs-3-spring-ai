package altocumulus.aidevs3.client.ag3nts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import altocumulus.aidevs3.model.common.ApiRequest;

@Component
public class C3ntralaClient {
    
    private static final String POST_URL = "https://c3ntrala.ag3nts.org/report";
    private static final String S01E05_URL = "https://c3ntrala.ag3nts.org/data/%s/cenzura.txt";
    private static final String S02E03_URL = "https://c3ntrala.ag3nts.org/data/%s/robotid.json";
    
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
        String url = String.format(S01E05_URL, apiKey);
        return getData(url);
    }

    public String getDataForS02e03() {
        String url = String.format(S02E03_URL, apiKey);
        return getData(url);
    }
    public String getApiKey() {
        return apiKey;
    }

    private String getData(String url) {
        String data = "";
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            data = response.getBody();
        } catch (Exception e) {
            System.out.println("Error getting data: " + e.getMessage());
        }
        return data;
    }
}