package altocumulus.aidevs3.service.chat;

/**
 * Enum representing various OpenAI GPT model names.
 * Each enum constant holds the official model ID string used for API calls.
 */
public enum GptModel {

    // GPT-4o models - OpenAI's latest and most advanced models
    GPT_4O("gpt-4o"),
    GPT_4O_MINI("gpt-4o-mini"),

    // GPT-4 Turbo models - Predecessor to GPT-4o, still powerful
    GPT_4_TURBO("gpt-4-turbo"), // General availability, replaces older preview models
    GPT_4_TURBO_PREVIEW("gpt-4-turbo-preview"), // General name for latest preview, often points to a specific dated version
    GPT_4_0125_PREVIEW("gpt-4-0125-preview"), // Example of a specific preview version
    GPT_4_1106_PREVIEW("gpt-4-1106-preview"), // Another example of a specific preview version
    GPT_4_VISION_PREVIEW("gpt-4-vision-preview"), // For tasks involving image understanding

    // GPT-4 models - Foundational powerful models
    GPT_4("gpt-4"),
    GPT_4_32K("gpt-4-32k"), // GPT-4 with a larger context window

    // GPT-3.5 models - Capable and cost-effective models
    GPT_3_5_TURBO("gpt-3.5-turbo"), // Flagship GPT-3.5 model
    GPT_3_5_TURBO_16K("gpt-3.5-turbo-16k"), // GPT-3.5 Turbo with a 16k context window
    GPT_3_5_TURBO_INSTRUCT("gpt-3.5-turbo-instruct"), // Instruct model for completion tasks
    GPT_3_5_TURBO_0125("gpt-3.5-turbo-0125"), // Specific version of GPT-3.5 Turbo
    GPT_3_5_TURBO_1106("gpt-3.5-turbo-1106"), // Another specific version of GPT-3.5 Turbo

    // o-series models - New reasoning models (example, names might evolve)
    O4_MINI("o4-mini"), // Faster, more affordable reasoning model
    O3("o3"); // Powerful reasoning model

    private final String modelId;

    /**
     * Constructor for the GptModel enum.
     * @param modelId The official string identifier for the GPT model.
     */
    private GptModel(String modelId) {
        this.modelId = modelId;
    }

    /**
     * Gets the official model ID string.
     * This is the string that should be used when making API calls to OpenAI.
     * @return The model ID string.
     */
    public String getModelId() {
        return modelId;
    }

    /**
     * Returns a string representation of the enum constant (the model ID).
     * @return The model ID string.
     */
    @Override
    public String toString() {
        return modelId;
    }
}
