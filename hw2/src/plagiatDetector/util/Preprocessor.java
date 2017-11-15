package plagiatDetector.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Preprocessor {

	public String preprocess(String data) {
		data = removeComments(data);
		data = removeWhitespaces(data);
		data = removeImports(data);
		data = removeModifiers(data);
		data = removeModifiers(data);
		data = renameVariables(data);
		data = renameMethods(data);
		data = replaceTypes(data);
		data = removeWhitespaces(data);
		return data;
	}

	public String removeWhitespaces(String data) {
		return data.replaceAll("[\\s]+", " ");
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
		Matcher matcher = Pattern.compile("[a-zA-Z_$]+[_$a-zA-Z0-9<>\\[\\]]*\\s+([_\\-a-zA-Z0-9]+)\\s*(=.*(;|,)|(;|,))").matcher(data);
		while (matcher.find()) {
			String variableName = matcher.group(1);
			data = data.replaceAll("([^a-zA-Z0-9]+)" + variableName + "([^a-zA-Z0-9])", "$1VARIABLE_NAME$2");
		}
		return data;
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
