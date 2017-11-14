package plagiatDetector.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PreprocessorTest {

    @Test
    public void testRemoveWhiteSpaces() {
        String before =
                "// some comment\n" +
                        "   import java.util.*\n\n" +
                        "     public   static    void   main(String[]     args)     {  \n" +
                        "\t\t\tdoStuff(); //       does stuff\n\n\n\n\n\n" +
                        "\t}\n" +
                        "}";
        String after =
                "// some comment\n" +
                        " import java.util.*\n" +
                        " public static void main(String[] args) { \n" +
                        " doStuff(); // does stuff\n" +
                        " }\n" +
                        "}";
        Preprocessor preprocessor = new Preprocessor();
        assertEquals(after, preprocessor.removeWhitespaces(before));
    }

    @Test
    public void testRemoveCommentsBasic() {
        String before =
                "// some comment\n" +
                        "import java.util.*\n\n" +
                        "public static void main(String[] args) {\n" +
                        "doStuff(); // does stuff\n" +
                        "}\n" +
                        "}";
        String after =
                "\n" +
                        "import java.util.*\n\n" +
                        "public static void main(String[] args) {\n" +
                        "doStuff(); \n" +
                        "}\n" +
                        "}";
        Preprocessor preprocessor = new Preprocessor();
        assertEquals(after, preprocessor.removeComments(before));
    }

    @Test
    public void testRemoveCommentsAdvanced() {
        String before =
                "/* some comment */\n" +
                        "import java.util.*\n\n" +
                        "/**\nMY METHOD THAT DOES COOL THINGS\n@returns NOTHING LOL\n*/" +
                        "public static void main(String[] args) {\n" +
                        "//doStuff(); // does stuff\n" +
                        "doOtherStuff() /* does other\nstuff*/\n" +
                        "}\n" +
                        "}//the end";
        String after =
                "\n" +
                        "import java.util.*\n\n" +
                        "" +
                        "public static void main(String[] args) {\n" +
                        "\n" +
                        "doOtherStuff() \n" +
                        "}\n" +
                        "}";
        Preprocessor preprocessor = new Preprocessor();
        assertEquals(after, preprocessor.removeComments(before));
    }

    @Test
    public void testRemoveImports() {
        String before =
                "// some comment\n" +
                        "import java.util.*\n" +
                        "import com.example.some.cool.thing.*\n\n" +
                        "import static junit.assert\n\n" +
                        "public static void main(String[] args) {\n" +
                        "doStuff(); // does stuff\n" +
                        "}\n" +
                        "}";
        String after =
                "// some comment\n" +
                        "" +
                        "\n" +
                        "\n" +
                        "public static void main(String[] args) {\n" +
                        "doStuff(); // does stuff\n" +
                        "}\n" +
                        "}";
        Preprocessor preprocessor = new Preprocessor();
        assertEquals(after, preprocessor.removeImports(before));
    }

    @Test
    public void testRemoveModifier() {
        String before =
                "import java.util.*\n\n" +
                        "private int x = 2;\n" +
                        "public int y = 2;\n" +
                        "private final int z = 2;\n" +
                        "protected Long l = 2L;\n" +
                        "public static void main(String[] args) {\n" +
                        "doStuff(); \n" +
                        "}\n" +
                        "}";
        String after =
                "import java.util.*\n\n" +
                        "int x = 2;\n" +
                        "int y = 2;\n" +
                        "int z = 2;\n" +
                        "Long l = 2L;\n" +
                        "static void main(String[] args) {\n" +
                        "doStuff(); \n" +
                        "}\n" +
                        "}";
        Preprocessor preprocessor = new Preprocessor();
        assertEquals(after, preprocessor.removeModifiers(before));
    }

    @Test
    public void testRenameVariables() {
        String before =
                "// some comment\n" +
                        "import java.util.*\n\n" +
                        "public static void main(String[] args) {\n" +
                        "int a = 2;\n" +
                        "int b = 3;\n" +
                        "String word = \"yap\";\n" +
                        "doStuff(); // does stuff\n" +
                        "MyType myVar = new MyType();\n" +
                        "MyType2 _myVar2 = new MyType2(a, b);\n" +
                        "double    d9000_some_long_varname_with_numbers_123_AND_CAPITALS    =   22.0002d;\n" +
                        "long l=0xABCDEFL;\n" +
                        "}\n" +
                        "}";
        String after =
                "// some comment\n" +
                        "import java.util.*\n\n" +
                        "public static void main(String[] args) {\n" +
                        "int VARIABLE_NAME = 2;\n" +
                        "int VARIABLE_NAME = 3;\n" +
                        "String VARIABLE_NAME = \"yap\";\n" +
                        "doStuff(); // does stuff\n" +
                        "MyType VARIABLE_NAME = new MyType();\n" +
                        "MyType2 VARIABLE_NAME = new MyType2(a, b);\n" +
                        "double VARIABLE_NAME = 22.0002d;\n" +
                        "long VARIABLE_NAME = 0xABCDEFL;\n" +
                        "}\n" +
                        "}";
        Preprocessor preprocessor = new Preprocessor();
        assertEquals(after, preprocessor.renameVariables(before));
    }

    @Test
    public void testRenameMethods() {
        String before =
                "// some comment\n" +
                        "import java.util.*\n\n" +
                        "public static void main(String[] args) {\n" +
                        "\tdoStuff(); // does stuff\n" +
                        "MyType myVar = new MyType();\n" +
                        "MyType2 _myVar2 = new MyType2(a, b);\n" +
                        "}\n" +
                        "public Float calcSomeStuff(float a, float b) {\nreturn new Float(a+b);\n}\n" +
                        "void doThings() {\n/*TODO*/\n}\n" +
                        "protected static MyType _a_really_cool_OPERATION(MyType a) {\n/*TODO*/\n}\n" +
                        "}";
        String after =
                "// some comment\n" +
                        "import java.util.*\n\n" +
                        "public static void METHOD_NAME(String[] args) {\n" +
                        "\tMETHOD_NAME(); // does stuff\n" +
                        "MyType myVar = new METHOD_NAME();\n" +
                        "MyType2 _myVar2 = new METHOD_NAME(a, b);\n" +
                        "}\n" +
                        "public Float METHOD_NAME(float a, float b) {\nreturn new METHOD_NAME(a+b);\n}\n" +
                        "void METHOD_NAME() {\n/*TODO*/\n}\n" +
                        "protected static MyType METHOD_NAME(MyType a) {\n/*TODO*/\n}\n" +
                        "}";
        Preprocessor preprocessor = new Preprocessor();
        assertEquals(after, preprocessor.renameMethods(before));
    }

}
