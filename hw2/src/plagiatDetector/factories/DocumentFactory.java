package plagiatDetector.factories;

import plagiatDetector.models.Document;
import plagiatDetector.util.Preprocessor;
import plagiatDetector.util.Shingler;
import plagiatDetector.util.Tokenizer;

public class DocumentFactory {

    private Tokenizer tokenizer;
    private Shingler shingler;
    private Preprocessor preprocessor;

    public DocumentFactory(Preprocessor preprocessor, Tokenizer tokenizer, Shingler shingler) {
        this.tokenizer = tokenizer;
        this.shingler = shingler;
        this.preprocessor = preprocessor;
    }

    public Document makeDocument(String name, String rawData) {
        return new Document(name, rawData, preprocessor, tokenizer, shingler);
    }
}
