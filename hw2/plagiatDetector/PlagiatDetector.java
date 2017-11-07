package plagiatDetector;

import java.util.Collection;
import java.util.List;

public class PlagiatDetector {
	
	private DocumentRepository documentRepository;
	private ShingleRepository shingleRepository;
	private Shingler shingler;
	private Tokenizer tokenizer;
	private final static int TOKENS_PER_SHINGLE = 3;
	
	public PlagiatDetector() {
		documentRepository = new DocumentRepository();
		shingleRepository = new ShingleRepository();
		shingler = new Shingler(TOKENS_PER_SHINGLE, shingleRepository);
		tokenizer = new Tokenizer();
	}
	
	public void addDocument(Document document) {
		document.setShingler(shingler);
		document.setTokenizer(tokenizer);
		document.prepare();
		documentRepository.add(document);
	}

	public List<Document> getAllDocuments() {
		return documentRepository.getAll();
	}

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
