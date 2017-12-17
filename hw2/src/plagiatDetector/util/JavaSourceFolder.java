package plagiatDetector.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Util class that can be used to read all java source code inside a given folder and concatenate all the code in a
 * single String so that further analysis is simplified and can be done by just using this one concatenated String
 * instead of having to deal with x files in y folders.
 */
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