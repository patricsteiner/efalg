package plagiatDetector.util;

import java.util.Arrays;
import java.util.List;

public class Tokenizer {
    public List<String> tokenize(String content) {
        return Arrays.asList(content.split("[ \\n\\r\\n\\t]+"));
    }
}
