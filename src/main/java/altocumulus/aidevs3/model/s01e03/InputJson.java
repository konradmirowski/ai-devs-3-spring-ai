package altocumulus.aidevs3.model.s01e03;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InputJson(
        String apikey, 
        String description, 
        String copyright, 
        @JsonProperty("test-data") List<TestData> testData
    ) {}