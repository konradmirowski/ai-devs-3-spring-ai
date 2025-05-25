package altocumulus.aidevs3.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile; // Ensure you have spring-test or similar dependency

import altocumulus.aidevs3.client.ag3nts.C3ntralaClient;
import altocumulus.aidevs3.client.openai.Whisper1Client;
import altocumulus.aidevs3.model.common.ApiRequest;
import altocumulus.aidevs3.service.chat.ChatService;

@Service
public class S02e01Service {

    private final C3ntralaClient c3ntralaClient;
    private final ChatService chatService;
    private final Whisper1Client whisper1Client;

    @Value("${c3ntrala.api.key}")
    private String apiKey;
 
    public S02e01Service(C3ntralaClient c3ntralaClient, ChatService chatService, Whisper1Client whisper1Client) {
        this.c3ntralaClient = c3ntralaClient;
        this.chatService = chatService;
        this.whisper1Client = whisper1Client;
    }

    public String getFlag() {
        try {
            String text = transcriptAudio();
            System.out.println("Transcript: " + text); //TODO: remove
            String prompt = buildPrompt(text);
            String streetName = chatService.askAI(prompt);
            System.out.println("AI answer: " + streetName); //TODO: remove
            String flag = sendDataToApi(streetName);
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            return "An unexpected error occurred: " + e.getMessage();
        }
    }

    private String transcriptAudio() throws Exception {
        MultipartFile multipartFile = getMultipartFileFromClasspath(
            "static/s02e01/adam.m4a",
            "adam.m4a",
            "audio/mp4"  // Changed to correct MIME type for m4a files
        );
        return whisper1Client.transcribeAudio(multipartFile);
    }

    public MultipartFile getMultipartFileFromClasspath(String classpathFilePath, String originalFilename, String contentType) throws Exception {
        Resource resource = new ClassPathResource(classpathFilePath);

        if (!resource.exists()) {
            throw new IOException("File not found at classpath: " + classpathFilePath);
        }

        try (InputStream inputStream = resource.getInputStream()) {
            byte[] content = inputStream.readAllBytes();
            return new MockMultipartFile(
                "file",  // This should be "file" as expected by OpenAI API
                originalFilename,
                contentType,
                content
            );
        }
    }

    private String buildPrompt(String text) {
        //TODO
        return "Please analyze the following transcript and answer what is the street of the Institute employing professor Maj:\n\n" + text;
    }

    private String sendDataToApi(String streetName) {
        ApiRequest request = new ApiRequest("mp3", apiKey, streetName);
        return c3ntralaClient.sendPost(request);
    }
}
