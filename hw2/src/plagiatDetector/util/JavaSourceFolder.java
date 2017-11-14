package plagiatDetector.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class JavaSourceFolder {

    private String name;
    private int fileCount;
    private int folderCount;
    private StringBuilder concatenatedContent = new StringBuilder();

    public JavaSourceFolder(File folder) throws IOException {
        name = folder.getName();
        readFolder(folder);
    }

    private void readFolder(File folder) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                folderCount++;
                readFolder(file);
            } else if (file.getName().endsWith(".java")) {
                fileCount++;
                concatenatedContent.append(new String(Files.readAllBytes(file.toPath())));
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getFileCount() {
        return fileCount;
    }

    public int getFolderCount() {
        return folderCount;
    }

    public String getConcatenatedContent() {
        return concatenatedContent.toString();
    }
}