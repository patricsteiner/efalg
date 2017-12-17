package plagiatDetector.factories;

import plagiatDetector.metrics.FileCountMetric;
import plagiatDetector.metrics.FolderCountMetric;
import plagiatDetector.models.Document;
import plagiatDetector.util.JavaSourceFolder;
import plagiatDetector.util.Preprocessor;
import plagiatDetector.util.Shingler;
import plagiatDetector.util.Tokenizer;

/**
 * Factory that creates a Document out of a given JavaSourceFolder.
 */
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

    public Document makeDocument(JavaSourceFolder javaSourceFolder) {
        Document document = makeDocument(javaSourceFolder.getName(), javaSourceFolder.getConcatenatedContent());
        document.addMetric(new FileCountMetric(javaSourceFolder.getFileCount()));
        document.addMetric(new FolderCountMetric(javaSourceFolder.getFolderCount()));
        return document;
    }
}
