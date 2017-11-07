package plagiatDetector;

import javax.print.Doc;
import java.util.Collection;
import java.util.HashSet;

public class PlagiatDetector {
	
	private DocumentRepository documentRepository;
	private ShingleRepository shingleRepository;
	private Shingler shingler;
	private final static int K = 3;
	
	public PlagiatDetector() {
		documentRepository = new DocumentRepository();
		shingleRepository = new ShingleRepository();
		shingler = new Shingler(K, shingleRepository);
	}
	
	public void addDocument(Document document) {
		document.setShingleIndices(shingler);
		documentRepository.add(document);
	}

	public Collection<Document> getAllDocuments() {
		return documentRepository.getAll();
	}

	public double similarity(Document document1, Document document2) {
		int equalShingles = 0;
		HashSet<Integer> union = new HashSet<>();
		for (int index : document1.getShingleIndices()) {
			union.add(index);
			// TODO using contains here is dumb, because O(n). Also, order should be considered! --> use SparseArray or sth.
			if (document2.getShingleIndices().contains(index))
				equalShingles++;
		}
		document2.getShingleIndices().forEach(i -> union.add(i));
		int totalShingles = document1.getShingleIndices().size() + document2.getShingleIndices().size();
		return (double) equalShingles / (totalShingles - equalShingles);
	}
}
