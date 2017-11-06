package plagiatDetector;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class DocumentRepository {
	
	private Collection<Document> documents;
	
	public DocumentRepository() {
		documents = new HashSet<>();
	}
	
	public void add(Document document) {
		documents.add(document);
	}
	
	public boolean contains(Document document) {
		return documents.contains(document);
	}
	
	public Collection<Document> getAll() {
		return Collections.unmodifiableCollection(documents);
	}
}
