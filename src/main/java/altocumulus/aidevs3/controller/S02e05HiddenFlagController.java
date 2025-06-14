package altocumulus.aidevs3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import altocumulus.aidevs3.service.s02e05hiddenflag.Decoder;
import altocumulus.aidevs3.service.s02e05hiddenflag.S02E05HiddenFlagService;

import java.util.Map;

@RestController
@RequestMapping("/s02e05hiddenflag")
public class S02e05HiddenFlagController {

    private final S02E05HiddenFlagService hiddenFlagService;
    private final Map<String, Decoder> decoders;

    @Autowired
    public S02e05HiddenFlagController(@Qualifier("S02E05HiddenFlag") S02E05HiddenFlagService hiddenFlagService, Map<String, Decoder> decoders) {
        this.hiddenFlagService = hiddenFlagService;
        // Spring will automatically inject a map of beans that implement the Decoder interface
        this.decoders = decoders;
    }

    /**
     * Solves the hidden flag puzzle by selecting a decoder and applying an XOR key.
     *
     * @param decoderName   The name of the decoder bean to use (e.g., "base58Decoder").
     * @param encodedCipher The encoded string to be decrypted.
     * @param key           The XOR key for decryption.
     * @return The formatted flag or an error message.
     */
    @GetMapping("/solve/{decoderName}")
    public String getFlag(
            @PathVariable String decoderName,
            @RequestParam String encodedCipher,
            @RequestParam String key
    ) {
        // Dynamically select the decoding strategy based on the bean name
        Decoder selectedDecoder = decoders.get(decoderName);

        if (selectedDecoder == null) {
            return String.format("{{FLG:ERROR_DECODER_NOT_FOUND_WITH_NAME_%s}}", decoderName);
        }

        return hiddenFlagService.findHiddenFlag(encodedCipher, key, selectedDecoder);
    }
}