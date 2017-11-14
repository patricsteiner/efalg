package plagiatDetector.util;

public class Preprocessor {

	public String preprocess(String data) {
		data = removeWhitespaces(data);
		data = removeComments(data);
		data = removeImports(data);
		data = removeModifiers(data);
		data = removeModifiers(data);
		data = renameVariables(data);
		data = renameMethods(data);
		data = replaceTypes(data);
		return data;
	}

	public String removeWhitespaces(String data) {
		data = data.replaceAll("[\t ]+", " ");
		return data.replaceAll("[\n\r\n]+", "\n");
	}

	// this regex was shamelessly stolen from https://stackoverflow.com/a/1740692/4030765
	public String removeComments(String data) {
		return data.replaceAll("//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/", "");
	}

	public String removeImports(String data) {
		return data.replaceAll("import .*;\n", "");
	}

	public String removeModifiers(String data) {
		return data.replaceAll("public |private |protected |final ", "");
	}

	public String renameVariables(String data) {
		data = data.replaceAll("([_\\-a-zA-Z0-9<>\\[\\]]+ )[ \t]*[_\\-a-zA-Z0-9]+[ \t]*=[ \t]*(.*);", "$1VARIABLE_NAME = $2;");
		return data.replaceAll("([_\\-a-zA-Z0-9<>\\[\\]]+ )[ \t]*[_\\-a-zA-Z0-9]+[ \t]*;", "$1VARIABLE_NAME;");
	}

	public String renameMethods(String data) {
		return data.replaceAll("(.*[ \t]+)[_\\-a-zA-Z0-9]+(\\(.*\\))", "$1METHOD_NAME$2");
	}

	public String replaceTypes(String data) {
		data = data.replaceAll("long|short|byte|Long|Short|Byte", "int");
		data = data.replaceAll("float|Float|Double", "double");
		return data;
	}

}
