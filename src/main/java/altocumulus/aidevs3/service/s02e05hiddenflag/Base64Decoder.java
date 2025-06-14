package altocumulus.aidevs3.service.s02e05hiddenflag;

import org.springframework.stereotype.Component;
import java.util.Base64;

@Component("base64Decoder") // The qualifier name for this bean
public class Base64Decoder implements Decoder {

    @Override
    public byte[] decode(String encodedString) {
        // We use the built-in Java implementation of Base64
        return Base64.getDecoder().decode(encodedString);
    }
}