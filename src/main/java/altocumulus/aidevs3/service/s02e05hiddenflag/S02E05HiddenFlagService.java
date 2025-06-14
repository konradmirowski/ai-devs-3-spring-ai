package altocumulus.aidevs3.service.s02e05hiddenflag;

import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;

@Service("S02E05HiddenFlag")
public class S02E05HiddenFlagService {

    /**
     * Decrypts the hidden flag based on an encoded string, an XOR key, and a decoding strategy.
     *
     * @param encodedString The input string encoded according to the given strategy.
     * @param xorKey        The key for the XOR operation.
     * @param decoder       The decoding strategy object (e.g., Base58, Base64).
     * @return The decrypted and formatted flag in {{FLG:RESULT}} format, or an error message.
     */
    public String findHiddenFlag(String encodedString, String xorKey, Decoder decoder) {
        if (encodedString == null || encodedString.isEmpty() || xorKey == null || xorKey.isEmpty() || decoder == null) {
            return "{{FLG:ERROR_INVALID_INPUT}}";
        }

        try {
            // Step 1: Decode the string using the provided strategy (e.g., Base58 or Base64).
            byte[] decodedBytes = decoder.decode(encodedString);

            // Step 2: Decrypt using the repeating key XOR.
            byte[] decryptedBytes = performRepeatingKeyXor(decodedBytes, xorKey);

            // Step 3: Convert the result to a String and format the flag.
            String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);
            return String.format("{{FLG:%s}}", decryptedText);

        } catch (Exception e) {
            // Handle errors, e.g., an invalid data format for the given decoder.
            return String.format("{{FLG:ERROR_DECODING_FAILED - %s}}", e.getMessage());
        }
    }

    /**
     * Performs the XOR operation on a byte array using a repeating key.
     *
     * @param data   The byte array to be decrypted.
     * @param keyStr The key as a String.
     * @return The decrypted byte array.
     */
    private byte[] performRepeatingKeyXor(byte[] data, String keyStr) {
        byte[] key = keyStr.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ key[i % key.length]);
        }
        return result;
    }
}
