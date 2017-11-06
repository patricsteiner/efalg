package plagiatDetector;

import java.util.Arrays;
import java.util.List;

public class Detector {
	
	private DocumentRepository documentRepository;
	private ShingleRepository shingleRepository;
	private Shingler shingler;
	
	public Detector() {
		documentRepository = new DocumentRepository();
		shingleRepository = new ShingleRepository();
		shingler = new Shingler(5, shingleRepository);
	}
	
	public void addDocument(Document document) {
		documentRepository.add(document);
		shingler.makeShingles(document);
	}
	
	public double similarity(Document document1, Document document2) {
		int equalShingles = 0;
		for (int index : document1.getShinlgeIndices()) {
			if (document2.getShinlgeIndices().contains(index)) // TODO using contains here is dumb, because O(n)
				equalShingles++;
		}
		return equalShingles / shingleRepository.getSize();
	}
}
