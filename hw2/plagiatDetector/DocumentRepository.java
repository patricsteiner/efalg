package plagiatDetector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class DocumentRepository {
	
	private List<Document> documents;
	
	public DocumentRepository() {
		documents = new ArrayList<>();
	}
	
	public void add(Document document) {
		documents.add(document);
	}
	
	public List<Document> getAll() {
		return Collections.unmodifiableList(documents);
	}
}
