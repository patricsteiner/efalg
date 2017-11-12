package plagiatDetector.util;

public class Preprocessor {

	public String preprocess(String data) {
		data = removeWhitespaces(data);
		data = removeComments(data);
		data = removeModifiers(data);
		data = renameVariables(data);
		return data;
	}

	public String removeWhitespaces(String data) {
		data = data.replaceAll("[\t ]+", " ");
		return data.replaceAll("[\n\r\n]+", "\n");
	}

	// this regex was shamelessly stolen from here: https://stackoverflow.com/a/1740692/4030765
	public String removeComments(String data) {
		return data.replaceAll("//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/", "");
	}

	public String removeModifiers(String data) {
		return data.replaceAll("public|private|protected|final", "");
	}

	public String renameVariables(String data) {
		return data.replaceAll("([_\\-a-zA-Z0-9]+ )[ \t]*[_\\-a-zA-Z0-9]+[ \t]*=[ \t]*(.*);", "$1VARIABLE_NAME = $2;");
	}

	public String renameMethods(String data) {
		return data.replaceAll("(.*[ \t]+)[_\\-a-zA-Z0-9]+(\\(.*\\))", "$1METHOD_NAME$2");
	}

}
