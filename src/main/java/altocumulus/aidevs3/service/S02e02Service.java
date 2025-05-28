package altocumulus.aidevs3.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import altocumulus.aidevs3.client.openai.text.GptModel;
import altocumulus.aidevs3.client.openai.text.TextClient;

@Service
public class S02e02Service {

    private static final String SYSTEM_PROMPT = 
    """
    You are a highly skilled expert in geography, urban planning, and geolocation, with in-depth knowledge of Polish cities and their distinctive urban layouts. Your primary task is to meticulously analyze the provided map fragments.

    **Chain of Thought Process:**
    To ensure the most accurate identification, follow these steps in your reasoning:

    1.  **Individual Map Analysis (Initial Pass - Fragment by Fragment):**
        * For each of the provided map fragments (Fragment 1, Fragment 2, etc.), analyze them **separately**.
        * Identify and name **all identifiable elements**, not limited to just street names. Crucially, look for:
            * **Street names, squares, and major thoroughfares.**
            * **Specific landmarks:** Churches, hospitals, railway stations, shopping centers, schools, administrative buildings, stadiums.
            * **Transportation infrastructure:** Bus stops, tram stops, train stations (including their specific names if legible), metro stations, railway tracks.
            * **Service points:** Recognizable shop names (if present and legible), service points, parks, rivers, bridges, unique green spaces, public facilities.
            * **Urban patterns:** Grid layouts, radial patterns, river bends, unique intersection designs, presence of historical vs. modern districts.
        * During this initial pass, note down any potential city hints or distinguishing features each fragment suggests. Do NOT jump to conclusions about the city yet.

    2.  **Integrative Analysis and City Hypothesis Generation (Combining All Fragments):**
        * Now, synthesize the information gathered from **all** map fragments collectively.
        * **Formulate hypotheses about potential Polish cities** that could match the combined features observed across the fragments.
        * **Crucially, be aware that one of the four provided map fragments MAY BE INTENTIONALLY INCORRECT** and come from a completely different Polish city. As part of this integrative analysis, identify and exclude this inconsistent fragment from the main hypothesis validation if such an outlier exists.
        * For each potential city hypothesis, cross-reference the identified landmarks, street names, and urban patterns from the *matching* fragments to verify their existence and relative positions within that specific city.

    3.  **Final Verification and Conclusion:**
        * Select the **single most probable Polish city** that consistently aligns with the majority of the map fragments.
        * **Double-check:** Ensure that *all* the key locations and features you recognized on the *matching* map fragments are definitively located within your chosen city. This step is critical for accuracy.

    **Your Response:**
    First, provide your detailed reasoning, explaining the key elements you recognized from each *matching* map fragment and how they led to your conclusion. If you identified an inconsistent map fragment, please mention it and briefly explain why it did not fit.
    **After presenting your full reasoning, clearly state the name of the identified Polish city.**

    Begin your analysis now.
    """;

    private static final String USER_PROMPT = 
    """
    Please analyze the attached four map fragments.
    """;

    private final TextClient textClient;
    private static final List<String> IMAGE_PATHS = List.of(
        "static/s02e02/map1.png",
        "static/s02e02/map2.png",
        "static/s02e02/map3.png",
        "static/s02e02/map4.png"
    );
 
    public S02e02Service(TextClient textClient) {
        this.textClient = textClient;
    }

    public String getFlag() {
        List<Media> images = new ArrayList<>();
        for (String path : IMAGE_PATHS) {
            images.add(getImage(path));
        }

        UserMessage userMessage = UserMessage.builder()
            .text(USER_PROMPT)
            .media(images)
            .build();
        
        String aiResponse = textClient.askAI(userMessage, SYSTEM_PROMPT, GptModel.GPT_4O);
        
        return aiResponse;
    }

    private Media getImage(String imagePath) {
        Resource imageResource = new ClassPathResource(imagePath);
        Media imageMedia = new Media(MimeTypeUtils.IMAGE_PNG, imageResource);
        return imageMedia;
    }

}
