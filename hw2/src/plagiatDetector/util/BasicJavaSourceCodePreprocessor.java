package plagiatDetector.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple Preprocessor that processes a given input (java source code) and "normalizes" the source code in a way that
 * trivial changes to a Document will not be considered as a real difference in the perspective of the PlagiatDetector.
 * For example: When someone plagiarizes a Document and just changes some variable names, the PlagiatDetector shall
 * be smart enough to notice this.
 * Preprocessing is done using regular expressions, which is neither super efficient nor mighty enough to completely
 * "normalize" any given code.A more sophisticated way of doing this kind of preprocessing would be to generate and
 * analyze the abstract syntax tree of the given source code. However, since this PlagiatDetector is laid out to compare
 * Documents of students in a class to each other (which will maybe be around 50), performance will not be an issue.
 */
public class BasicJavaSourceCodePreprocessor implements Preprocessor {

	/**
	 * Does basic preprocessing to java source code by removing common changes that someone who plagiarizes a Document
     * would likely do.
	 * @param data the java source code.
	 * @return the processed java source code that can be compared to other Documents.
	 */
	@Override
	public String preprocess(String data) {
		data = removeComments(data);
		data = removeWhitespaces(data);
		data = removeImports(data);
		data = removeModifiers(data);
		data = renameVariables(data);
		data = renameMethods(data);
		data = replaceTypes(data);
		data = removeWhitespaces(data);
		return data;
	}

    /**
     * Removes all unecessary whitespaces in the code and replaces them by a single whitespace character.
     * @param data the java source code.
     * @return the String after the replacement took place.
     */
    public String removeWhitespaces(String data) {
		return data.replaceAll("[\\s]+", " ");
	}

	/**
	 * Removes all source code comments. This regex was shamelessly stolen from https://stackoverflow.com/a/1740692/4030765
	 * @param data the java source code.
	 * @return the String after the replacement took place.
	 */
    public String removeComments(String data) {
		return data.replaceAll("//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/", "");
	}

	/**
	 * Removes all import statements.
	 * @param data the java source code.
	 * @return the String after the replacement took place.
	 */
    public String removeImports(String data) {
		return data.replaceAll("import .*;\n", "");
	}

	/**
	 * Removes public, private, protected and final modifiers.
	 * @param data the java source code.
	 * @return the String after the replacement took place.
	 */
    public String removeModifiers(String data) {
		return data.replaceAll("public |private |protected |final ", "");
	}

	/**
	 * Tries to find all variable names in the source code and replaces them by VARIABLE_NAME.
	 * @param data the java source code.
	 * @return the String after the replacement took place.
	 */
    public String renameVariables(String data) {
		Matcher matcher = Pattern.compile("[a-zA-Z_$]+[_$a-zA-Z0-9<>\\[\\]]*\\s+([_\\-a-zA-Z0-9]+)\\s*(=.*(;|,)|(;|,))").matcher(data);
		while (matcher.find()) {
			String variableName = matcher.group(1);
			data = data.replaceAll("([^a-zA-Z0-9]+)" + variableName + "([^a-zA-Z0-9])", "$1VARIABLE_NAME$2");
		}
		return data;
	}

	/**
	 * Tries to find all method names in the source code and replaces them by METHOD_NAME.
	 * @param data the java source code.
	 * @return the String after the replacement took place.
	 */
    public String renameMethods(String data) {
		return data.replaceAll("(.*[ \t]+)[_\\-a-zA-Z0-9]+(\\(.*\\))", "$1METHOD_NAME$2");
	}

	/**
	 * Replaces numbers types to either int or double.
	 * @param data the java source code.
	 * @return the String after the replacement took place.
	 */
    public String replaceTypes(String data) {
		data = data.replaceAll("long|short|byte|Long|Short|Byte", "int");
		data = data.replaceAll("float|Float|Double", "double");
		return data;
	}

}
