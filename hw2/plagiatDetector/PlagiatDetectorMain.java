package plagiatDetector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlagiatDetectorMain {

	public static void main(String[] args) throws IOException {
		PlagiatDetector plagiatDetector = new PlagiatDetector();

		File testdata = new File("hw2/testdata");

		List<Document> myDocs = new ArrayList<>();

		for (File file : testdata.listFiles()) {
			String rawContent = new String(Files.readAllBytes(file.toPath()));
			myDocs.add(new Document(rawContent));
		}

		myDocs.forEach(plagiatDetector::addDocument);

		System.out.println(plagiatDetector.similarity(myDocs.get(2), myDocs.get(3)));

		myDocs.forEach(d -> System.out.println(d.getShingleIndices().stream().sorted().map(String::valueOf).reduce((i, i2) -> i + " " + i2)));
	}

}
