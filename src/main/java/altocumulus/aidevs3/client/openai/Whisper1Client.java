package altocumulus.aidevs3.client.openai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException; 
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import altocumulus.aidevs3.model.openai.WhisperResponse;

import java.io.IOException;

@Component
public class Whisper1Client {

    private static final String OPENAI_AUDIO_TRANSCRIPTION_URL = "https://api.openai.com/v1/audio/transcriptions";

    private final RestTemplate restTemplate;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Autowired
    public Whisper1Client(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String transcribeAudio(MultipartFile file) {
        try {
            HttpHeaders mainHeaders = buildMainRequestHeaders();
            MultiValueMap<String, Object> multipartBody = buildMultipartBody(file);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartBody, mainHeaders);

            ResponseEntity<WhisperResponse> response = restTemplate.exchange(
                OPENAI_AUDIO_TRANSCRIPTION_URL,
                HttpMethod.POST,
                requestEntity,
                WhisperResponse.class
            );

            if (response.getBody() != null && response.getBody().text() != null) {
                return response.getBody().text();
            }
            
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("OpenAI API Error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("File processing error: " + e.getMessage());
        } catch (RestClientException e) {
            System.err.println("Error during transcription (RestClientException): " + e.getMessage());
        }
        return "Whisper1Client::transcribeAudio::ERROR";
    }

    private HttpHeaders buildMainRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        // No need to set Content-Type: multipart/form-data here; RestTemplate will do it.
        return headers;
    }

    private MultiValueMap<String, Object> buildMultipartBody(MultipartFile file) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // File part
        HttpHeaders filePartHeaders = new HttpHeaders();
        filePartHeaders.setContentType(MediaType.parseMediaType(file.getContentType()));

        // Using ByteArrayResource with overridden getFilename to ensure Content-Disposition has filename
        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) { // IOException can occur here
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        HttpEntity<ByteArrayResource> fileEntity = new HttpEntity<>(fileResource, filePartHeaders);
        body.add("file", fileEntity);

        // Model part
        body.add("model", "whisper-1");
        // You could add other optional parameters here if needed:
        // body.add("language", "en");
        // body.add("response_format", "json"); // default is json for WhisperResponse

        return body;
    }
}