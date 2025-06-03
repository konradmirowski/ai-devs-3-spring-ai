package altocumulus.aidevs3.service;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import altocumulus.aidevs3.client.ag3nts.C3ntralaClient;
import altocumulus.aidevs3.client.openai.Whisper1Client;
import altocumulus.aidevs3.client.openai.text.GptModel;
import altocumulus.aidevs3.client.openai.text.TextClient;
import altocumulus.aidevs3.model.common.ApiRequest;
import altocumulus.aidevs3.model.s02e04.CategoryAnswer;

@Service
public class S02e04Service {

    private static final String SYSTEM_PROMPT_EXTRACT_TXT_FROM_IMG = """
        You are a headless, silent Optical Character Recognition (OCR) engine. Your only purpose is to extract text from a provided image. You must follow these rules without deviation:

        1.  **If the image contains text:** Your response must be **only** the raw, transcribed text. Transcribe the text exactly as it appears, preserving all line breaks, spacing, and punctuation. Do not include **any** introductory or concluding remarks, explanations, or apologies. Your output must begin with the first character of the transcribed text and end with the last character.

        2.  **If the image does not contain text:** Your response must be the single, exact phrase: `No text found in the image.` Do not provide any other information.
    """;
    private static final String USER_PROMPT_EXTRACT_TXT_FROM_IMG = "Please analyze the attached image and extract any visible text.";
    private static final String SYSTEM_PROMPT_ANALYZE_TXT = """
        You are a highly specialized data triage analyst. Your sole function is to analyze a given text and categorize it into one of three categories: `People`, `Hardware`, or `IGNORE`. You must adhere to the following rules without exception.

        **1. Categorization Rules:**

        * **`People`**: Assign this category ONLY if the text contains information about:
            * The capture or detainment of individuals.
            * Clear physical traces of human presence (e.g., "footprints found near the generator," "an abandoned campsite," "a diary was discovered").
            * *Exclusion*: Do not use this category for simple mentions of names, team rosters, or general communication between people.

        * **`Hardware`**: Assign this category ONLY if the text describes a fault, malfunction, or physical damage to a piece of hardware.
            * This includes issues like overheating, power failures, broken components, physical unresponsiveness, or smoke/sparks.
            * *Crucial Exclusion*: You must distinguish this from software issues. If the text describes an application crash, a virus, a software bug, or a driver error, it does NOT belong in this category unless a hardware fault is stated as the cause.

        * **`IGNORE`**: If the text does not strictly and unambiguously meet the criteria for either `People` or `Hardware`, you MUST assign this category. This is the default category for all other information, including software issues, mission reports, weather updates, or general logs.

        **2. Output Format:**

        * Your response must be a **single word only**.
        * The only valid responses are `People`, `Hardware`, or `IGNORE`.
        * Do not provide any comments, explanations, or punctuation. Your entire response must be one of those three exact words.
    """;
    

    @Value("${c3ntrala.api.key}")
    private String apiKey;

    private final C3ntralaClient c3ntralaClient;
    private final TextClient textClient;
    private final Whisper1Client whisper1Client;

    public S02e04Service(C3ntralaClient c3ntralaClient, TextClient textClient, Whisper1Client whisper1Client) {
        this.c3ntralaClient = c3ntralaClient;
        this.textClient = textClient;
        this.whisper1Client = whisper1Client;
    }

    public String getFlag() {
        String flag = "Flag could not be retrieved. Please try again later.";
        try {
            Resource[] resources = loadResourceFiles("classpath:static/s02e04/*");
            
            List<String> people = new ArrayList<>();
            List<String> hardware = new ArrayList<>();

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                String extension = StringUtils.getFilenameExtension(filename);
                byte[] content = resource.getInputStream().readAllBytes();

                switch (extension.toLowerCase()) {
                    case "png" -> analyzeImage(resource, filename, people, hardware);
                    case "txt" -> analyzeText(content, filename, people, hardware);
                    case "mp3" -> analyzeAudio(content, filename, people, hardware);
                }
            }

            System.out.println("people: " + people);
            System.out.println("hardware: " + hardware);
            flag = sendDataToApi(people, hardware);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process files: " + e.getMessage(), e);
        }
        return flag;
    }

    private Resource[] loadResourceFiles(String pattern) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        return resolver.getResources(pattern);
    }

    private void analyzeImage(Resource resource, String filename, List<String> people, List<String> hardware) {
        Media imageMedia = new Media(MimeTypeUtils.IMAGE_PNG, resource);
        UserMessage userMessage = UserMessage.builder()
            .text(USER_PROMPT_EXTRACT_TXT_FROM_IMG)
            .media(imageMedia)
            .build();
        String text = textClient.askAI(userMessage, SYSTEM_PROMPT_EXTRACT_TXT_FROM_IMG, GptModel.GPT_4O);
        analyzeText(text, filename, people, hardware);
    }

    private void analyzeText(byte[] content, String filename, List<String> people, List<String> hardware) {
        String text = new String(content, StandardCharsets.UTF_8);
        analyzeText(text, filename, people, hardware);
    }

    private void analyzeAudio(byte[] content, String filename, List<String> people, List<String> hardware) {
        MultipartFile multipartFile = new MockMultipartFile(
            "file",  // This should be "file" as expected by OpenAI API
            filename,
            "audio/mp3",
            content
        );
        String text = whisper1Client.transcribeAudio(multipartFile);
        analyzeText(text, filename, people, hardware);
    }

    private void analyzeText(String text, String filename, List<String> people, List<String> hardware) {
        String category = textClient.askAI(text, SYSTEM_PROMPT_ANALYZE_TXT, GptModel.GPT_4_1);
        // System.out.println("\n===============================");
        // System.out.println("Filename: " + filename);
        // System.out.println("Text: " + text);
        // System.out.println("Category: " + category);
        // System.out.println("===============================\n");    
        switch (category) {
            case "People" -> people.add(filename);
            case "Hardware" -> hardware.add(filename);
        }    
    }

    private String sendDataToApi(List<String> people, List<String> hardware) {
        CategoryAnswer categoryAnswer = new CategoryAnswer(people, hardware);
        ApiRequest request = new ApiRequest("kategorie", apiKey, categoryAnswer);
        return c3ntralaClient.sendPost(request);
    }
}
