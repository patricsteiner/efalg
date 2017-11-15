package plagiatDetector;

import plagiatDetector.factories.DocumentFactory;
import plagiatDetector.models.Document;
import plagiatDetector.repositories.DocumentRepository;
import plagiatDetector.repositories.ShingleRepository;
import plagiatDetector.util.Preprocessor;
import plagiatDetector.util.Shingler;
import plagiatDetector.util.Tokenizer;

import java.util.List;

public class PlagiatDetector {
	
	private DocumentRepository documentRepository;
	private ShingleRepository shingleRepository;
	private DocumentFactory documentFactory;
	private final static int TOKENS_PER_SHINGLE = 5;
	
	public PlagiatDetector() {
		documentRepository = new DocumentRepository();
		shingleRepository = new ShingleRepository();
		Shingler shingler = new Shingler(TOKENS_PER_SHINGLE, shingleRepository);
		Tokenizer tokenizer = new Tokenizer();
		Preprocessor preprocessor = new Preprocessor();
		documentFactory = new DocumentFactory(preprocessor, tokenizer, shingler);
	}
	
	public void addDocument(Document document) {
		documentRepository.add(document);
	}

	public List<Document> getAllDocuments() {
		return documentRepository.getAll();
	}

	public DocumentFactory getDocumentFactory() {
		return documentFactory;
	}

	/**
	 * Calculates the jaccard similarity of two documents by dividing the intersection of shingles to the union of shingles.
	 * @param document1
	 * @param document2
	 * @return Similarity of the documents as a value between 0 and 1 (both inclusive).
	 */
	public double similarity(Document document1, Document document2) {
		int equalShingles = 0;
		for (int index : document1.getShingleIndices()) {
			if (document2.getShingleIndices().contains(index))
				equalShingles++;
		}
		int totalShingles = document1.getShingleIndices().size() + document2.getShingleIndices().size();
		return (double) equalShingles / (totalShingles - equalShingles);
	}
}
