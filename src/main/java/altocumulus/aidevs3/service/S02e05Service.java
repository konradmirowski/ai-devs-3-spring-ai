package altocumulus.aidevs3.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import altocumulus.aidevs3.client.ag3nts.C3ntralaClient;
import altocumulus.aidevs3.client.openai.Whisper1Client;
import altocumulus.aidevs3.client.openai.text.GptModel;
import altocumulus.aidevs3.client.openai.text.TextClient;

@Service
public class S02e05Service {
    
    private static final String ARTICLE_MD_FILENAME = "s02e05_article.md";

    private static final String SYSTEM_PROMPT = """
        
    """;

    @Value("${c3ntrala.api.key}")
    private String apiKey;

    private final C3ntralaClient c3ntralaClient;
    private final TextClient textClient;
    private final Whisper1Client whisper1Client;

    public S02e05Service(C3ntralaClient c3ntralaClient, TextClient textClient, Whisper1Client whisper1Client) {
        this.c3ntralaClient = c3ntralaClient;
        this.textClient = textClient;
        this.whisper1Client = whisper1Client;
    }

    public String getFlag() {
        String flag = "Flag could not be retrieved. Please try again later.";
        
        String articleMarkdown = getArticleMarkdownFromFile();
        if (articleMarkdown == null || StringUtils.isEmpty(articleMarkdown)) {
            String article = c3ntralaClient.getDataForS02e05Article();
            articleMarkdown = convertArticleToMarkdown(article);
            saveTextToFile(articleMarkdown);
        }
        
        String questions = c3ntralaClient.getDataForS02e05Questions();
        //TODO: Extract questions and based on the article, provide answers from llm

        String answers = textClient.askAI(questions, articleMarkdown, GptModel.GPT_4O);

        return flag;   
    }

    private String getArticleMarkdownFromFile() {
        String markdown = null;
        //TODO: Implement logic to read the Markdown file containing the article. Filename: ARTICLE_MD_FILENAME
        return markdown;
    }

    private String convertArticleToMarkdown(String article) {
        String markdown = "";
        //TODO : Implement logic to convert the article content to Markdown format
        // * Tekst: Przetwórz treść HTML - usuń zbędne tagi i formatowanie. Możesz użyć konwertera HTML do MD dostępnego w Twoim języku programowania. 
        // * Grafiki: Pobierz obrazy. Użyj modelu LLM Vision do wygenerowania opisów obrazów. Pamiętaj o kontekście – uwzględnij np. podpisy pod zdjęciami.
        // * Dźwięki: Pobierz pliki dźwiękowe (MP3). Użyj narzędzia do transkrypcji mowy na tekst (np. Whisper) aby przekonwertować dźwięk na tekst.
        // * Zapisz wszystko w jednym pliku: Stwórz jeden plik (np. Markdown), który zawiera:
        //     * Przetworzony tekst artykułu. 
        //     * Opisy obrazów (zamiast samych obrazów). 
        //     * Transkrypcje nagrań (zamiast plików dźwiękowych).
        return markdown;
    }

    private void saveTextToFile(String text) {
        //TODO: Implement logic to save the text to a file with the filename ARTICLE_MD_FILENAME.
    }

}
