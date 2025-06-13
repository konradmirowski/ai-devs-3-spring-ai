package altocumulus.aidevs3.service;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;

import altocumulus.aidevs3.client.ag3nts.C3ntralaClient;
import altocumulus.aidevs3.client.openai.Whisper1Client;
import altocumulus.aidevs3.client.openai.text.GptModel;
import altocumulus.aidevs3.client.openai.text.TextClient;
import altocumulus.aidevs3.model.common.ApiRequest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.nio.charset.StandardCharsets;

@Service
public class S02e05Service {
    
    private static final String S02E05_FILEPATH = "src/main/resources/static/s02e05";
    private static final String ARTICLE_MD_FILENAME = "s02e05_article.md";

    private static final String IMG_ANALYZER_SYSTEM_PROMPT = """
        You are a visual analysis AI. Your task is to analyze an image and its accompanying caption provided by the user. Synthesize the visual information from the image and the contextual information from the caption into a single, short, and concise plain text description.

        RULES:
        1.  Your output must be plain text only.
        2.  Your output must be a short, direct description.
        3.  Do not use Markdown, bullet points, or any other formatting.
        4.  Do not add any conversational text or introductory phrases like "Here is the description:". Respond only with the description itself.
    """;

    private static final String ARTICLE_ANALYZER_SYSTEM_PROMPT = """
        You are a precision AI assistant. Your sole task is to answer user questions based exclusively on the context they provide in the user prompt.

        **Core Rules:**
        1.  Answer in Polish.
        2.  Each answer must be concise and consist of a single sentence.
        3.  Your entire response MUST be a single, complete, and syntactically correct JSON object.
        4.  Do not add any extra explanations, comments, or formatting markers (like ```json) before or after the JSON object.

        Your response must strictly adhere to the following structure, populating the values based on the user's provided article and questions:
        {
            "01": "The answer to the first question in one sentence.",
            "02": "The answer to the second question in one sentence.",
            "03": "The answer to the third question in one sentence."
        }
    """;

    private static final String ARTICLE_ANALYZER_USER_PROMPT = """
        Please generate your JSON response based on the following article and questions.

        ### ARTICLE TEXT (CONTEXT) ###

        %s

        ---
        ### QUESTIONS TO ANALYZE ###

        %s
    """;

    @Value("${c3ntrala.api.key}")
    private String apiKey;

    private final C3ntralaClient c3ntralaClient;
    private final TextClient textClient;
    private final Whisper1Client whisper1Client;
    private final FileDownloadService fileDownloadService;
    private final ObjectMapper objectMapper;

    public S02e05Service(C3ntralaClient c3ntralaClient, TextClient textClient, Whisper1Client whisper1Client, FileDownloadService fileDownloadService, ObjectMapper objectMapper) {
        this.c3ntralaClient = c3ntralaClient;
        this.textClient = textClient;
        this.whisper1Client = whisper1Client;
        this.fileDownloadService = fileDownloadService;
        this.objectMapper = objectMapper;
    }

    public String getFlag() {
        
        String articleMarkdown = getArticleMarkdownFromFile();
        if (articleMarkdown == null || StringUtils.isEmpty(articleMarkdown)) {
            String article = c3ntralaClient.getDataForS02e05Article();
            article = replaceMediaNodes(article);
            articleMarkdown = convertArticleToMarkdown(article);
            saveTextToFile(articleMarkdown);
        }
        String questions = c3ntralaClient.getDataForS02e05Questions();
        String userPrompt = String.format(ARTICLE_ANALYZER_USER_PROMPT, articleMarkdown, questions);
        String answers = textClient.askAI(userPrompt, ARTICLE_ANALYZER_SYSTEM_PROMPT, GptModel.GPT_4O_MINI);

        String flag = sendDataToApi(answers); 
        return flag;
    }

    private String getArticleMarkdownFromFile() {
        Path filePath = Path.of(S02E05_FILEPATH, ARTICLE_MD_FILENAME);
        try {
            if (!Files.exists(filePath)) {
                return null;
            }
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read markdown file: " + e.getMessage(), e);
        }
    }

    private String replaceMediaNodes(String article) {
        Document doc = Jsoup.parse(article);

        //figure
        Elements figureElements = doc.select("figure");
        for (Element element : figureElements) {
            String src = element.getElementsByTag("img").get(0).attr("src");
            String figcaption = element.getElementsByTag("figcaption").get(0).text();

            URI imageUri = URI.create("https://c3ntrala.ag3nts.org/dane/" + src);
            Media imageMedia = new Media(MimeTypeUtils.IMAGE_PNG, imageUri);
            UserMessage userMessage = UserMessage.builder()
                .text("Caption: " + figcaption)
                .media(imageMedia)
                .build();
            String aiDescription = textClient.askAI(userMessage, IMG_ANALYZER_SYSTEM_PROMPT, GptModel.GPT_4O_MINI);

            Element blockquote = new Element("blockquote");
            blockquote.append("<p><strong>[OPIS OBRAZU]</strong></p>");
            blockquote.append("<p><strong>Źródło:</strong> <code>" + imageUri.toString() + "</code></p>");
            blockquote.append("<p><strong>Figcaption:</strong> " + figcaption + "</p>");
            blockquote.append("<p><strong>AI description:</strong> " + aiDescription + "</p>");

            element.after(blockquote);
            element.remove();
        }

        //audio
        Elements audioElements = doc.select("audio");
        for (Element element : audioElements) {
            try {
                String src = element.getElementsByTag("source").get(0).attr("src");
                String srcUrl = "https://c3ntrala.ag3nts.org/dane/" + src;
                MultipartFile audioFile = fileDownloadService.downloadFile(srcUrl, S02E05_FILEPATH);
                String transcribedAudio = whisper1Client.transcribeAudio(audioFile);

                Element blockquote = new Element("blockquote");
                blockquote.append("<p><strong>[TRANSKRYPCJA AUDIO]</strong></p>");
                blockquote.append("<p><strong>Źródło:</strong> <code>" + srcUrl + "</code></p>");
                blockquote.append("<p><strong>Treść (AI):</strong> " + transcribedAudio + "</p>");

                element.after(blockquote);
                element.remove();
            } catch (Exception e) {
                System.err.println("Error processing audio element: " + e.getMessage());
            }
        }

        return doc.html();
    }

    private String convertArticleToMarkdown(String article) {
        FlexmarkHtmlConverter converter = FlexmarkHtmlConverter.builder().build();
        String markdown = converter.convert(article);
        return markdown;
    }

    private void saveTextToFile(String text) {
        try {
            Path resourcePath = Path.of(S02E05_FILEPATH);
            Files.createDirectories(resourcePath);
            
            Path filePath = resourcePath.resolve(ARTICLE_MD_FILENAME);
            Files.writeString(filePath, text, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save markdown file: " + e.getMessage(), e);
        }
    }

    private String sendDataToApi(String answer) {
        try {
            Map<String, Object> answerObject = objectMapper.readValue(answer, new TypeReference<>() {});
            ApiRequest request = new ApiRequest("arxiv", apiKey, answerObject);
            return c3ntralaClient.sendPost(request);
        } catch (IOException e) {
            System.err.println("Error parsing answer JSON: " + e.getMessage());
            return "Error parsing answer JSON: " + e.getMessage();
        }
    }
}
