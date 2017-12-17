package plagiatDetector.util;

import java.util.Arrays;
import java.util.List;

/**
 * Util class that splits a String into tokens, using whitespaces as a delimiter.
 */
public class Tokenizer {
    public List<String> tokenize(String content) {
        return Arrays.asList(content.split("[ \\n\\r\\n\\t]+"));
    }
}
