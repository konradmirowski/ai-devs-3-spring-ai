package altocumulus.aidevs3.service.s02e05hiddenflag;

import io.ipfs.multibase.Base58;
import org.springframework.stereotype.Component;

@Component("base58Decoder") // The qualifier name for this bean
public class Base58Decoder implements Decoder {

    @Override
    public byte[] decode(String encodedString) {
        // Logic specific to Base58 decoding
        return Base58.decode(encodedString);
    }
}