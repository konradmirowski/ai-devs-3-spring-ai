package altocumulus.aidevs3.client.openai;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageClient {

    private final OpenAiImageModel openAiImageClient;
    
    @Autowired
    public ImageClient(OpenAiImageModel openAiImageClient) {
        this.openAiImageClient = openAiImageClient;
    }

    public String generateImage(String imagePrompt) {
        if (StringUtils.isEmpty(imagePrompt)) {
            return StringUtils.EMPTY;
        }

        ImageResponse response = openAiImageClient.call(
                new ImagePrompt(imagePrompt,
                OpenAiImageOptions.builder()
                        .quality("hd")
                        .N(1)
                        .height(1024)
                        .width(1024)
                        .responseFormat("url")
                        .build())

        );
        String url = response.getResult().getOutput().getUrl();
        return url;
    }

}
