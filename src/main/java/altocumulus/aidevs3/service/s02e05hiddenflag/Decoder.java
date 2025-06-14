package altocumulus.aidevs3.service.s02e05hiddenflag;

/**
 * A functional interface for a decoding strategy.
 * Each implementation is responsible for decoding a string using a specific algorithm.
 */
@FunctionalInterface
public interface Decoder {
    byte[] decode(String encodedString) throws Exception;
}