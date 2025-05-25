package altocumulus.aidevs3.client.openai;

import java.util.Map;

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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import altocumulus.aidevs3.model.openai.WhisperResponse;

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
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            HttpHeaders filePartHeaders = new HttpHeaders();
            filePartHeaders.setContentType(MediaType.parseMediaType(file.getContentType()));
            // Using ByteArrayResource with overridden getFilename to ensure Content-Disposition has filename
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            HttpEntity<ByteArrayResource> fileEntity = new HttpEntity<>(fileResource, filePartHeaders);
            body.add("file", fileEntity);
            body.add("model", "whisper-1");

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

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
            String errorBody = e.getResponseBodyAsString();
            System.err.println("OpenAI API Error (" + e.getStatusCode() + "): " + errorBody);
        } catch (Exception e) { // Catch IOException from getBytes() and other RestClientExceptions
            System.err.println("Error during transcription: " + e.getMessage());
        }
        return "Audio transcription failed.";
    }
}
