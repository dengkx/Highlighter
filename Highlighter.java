package JavaSyntaxHighterDemo;
 
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
 
/**
 * @author Kevin Tse
 * @version 0.1
 */
public class JavaSyntaxHighlighter {
 
    public static int STATE_TEXT = 1; // 普通文本
    public static int STATE_DOUBLE_QUOTE = 2; // 双引号
    public static int STATE_SINGLE_QUOTE = 3; // 单引号
    public static int STATE_MULTI_LINE_COMMENT = 4; // 多行注释
    public static int STATE_LINE_COMMENT = 5; // 单行注释
 
    private int lineNumber; // 行号
    private boolean enableLineNumber = true; // 开启行号标志
 
    public boolean isEnableLineNumber() {
        return enableLineNumber;
    }
 
    // line numbers are printed by default.
    // but this behavior can be disabled by invoking this method and setting the
    // flag to false
    public void setEnableLineNumber(boolean enableLineNumber) {
        this.enableLineNumber = enableLineNumber;
    }
 
    String[] literalArray = { "null", "true", "false" };    //字面常量
    String[] keywordArray = { "abstract", "break", "case", "catch", "class",
            "const", "continue", "default", "do", "else", "extends", "final",
            "finally", "for", "goto", "if", "implements", "import",
            "instanceof", "interface", "native", "new", "package", "private",
            "protected", "public", "return", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws", "transient",
            "try", "volatile", "while" };                  //关键词
    String[] primitiveTypeArray = { "boolean", "char", "byte", "short", "int",
            "long", "float", "double", "void" };           //原始数据类型
 
    Set<String> literalSet = new HashSet<String>(Arrays.asList(literalArray));
    Set<String> keywordSet = new HashSet<String>(Arrays.asList(keywordArray));
    Set<String> primitiveTypeSet = new HashSet<String>(Arrays
            .asList(primitiveTypeArray));
 
    public String process(String src) {
        int currentState = STATE_TEXT;
        int identifierLength = 0;
        int currentIndex = -1;
        char currentChar = 0;
        String identifier = "";
        StringBuffer out = new StringBuffer();
 
        while (++currentIndex != src.length() - 1) {
            currentChar = src.charAt(currentIndex);
            if (Character.isJavaIdentifierPart(currentChar)) {
                out.append(currentChar);
                ++identifierLength;
                continue;
            }
            if (identifierLength > 0) {
                identifier = out.substring(out.length() - identifierLength);
                if (currentState == STATE_TEXT) {
                    if (literalSet.contains(identifier)) { // identifier is a
                                                            // literal
                        out.insert(out.length() - identifierLength,
                                "<div class=\"literalStyle\">");
                        out.append("</div>");
                    } else if (keywordSet.contains(identifier)) { // identifier
                                                                    // is a
                                                                    // keyword
                        out.insert(out.length() - identifierLength,
                                "<div class=\"keywordStyle\">");
                        out.append("</div>");
                    } else if (primitiveTypeSet.contains(identifier)) { // identifier
                                                                        // is a
                                                                        // primitive
                                                                        // type
                        out.insert(out.length() - identifierLength,
                                "<div class=\"primitiveTypeStyle\">");
                        out.append("</div>");
                    } else if (identifier.equals(identifier.toUpperCase())
                            && !Character.isDigit(identifier.charAt(0))) { // identifier
                                                                            // is
                                                                            // a
                                                                            // constant
                        out.insert(out.length() - identifierLength,
                                "<div class=\"constantStyle\">");
                        out.append("</div>");
                    } else if (Character.isUpperCase(identifier.charAt(0))) { // identifier
                                                                                // is
                                                                                // non-primitive
                                                                                // type
                        out.insert(out.length() - identifierLength,
                                "<div class=\"nonPrimitiveTypeStyle\">");
                        out.append("</div>");
                    }
                }
            }
 
            switch (currentChar) {
            // because I handle the "greater than" and "less than" marks
            // somewhere else, I comment them out here
            // case '<':
            // out.append("&lt;");
            // break;
            // case '>':
            // out.append("&gt;");
            // break;
            case '\"':
                out.append('\"');
                if (currentState == STATE_TEXT) {
                    currentState = STATE_DOUBLE_QUOTE;
                    out.insert(out.length() - ("\"").length(),
                            "<div class=\"doubleQuoteStyle\">");
                } else if (currentState == STATE_DOUBLE_QUOTE) {
                    currentState = STATE_TEXT;
                    out.append("</div>");
                }
                break;
            case '\'':
                out.append("\'");
                if (currentState == STATE_TEXT) {
                    currentState = STATE_SINGLE_QUOTE;
                    out.insert(out.length() - ("\'").length(),
                            "<div class=\"singleQuoteStyle\">");
                } else if (currentState == STATE_SINGLE_QUOTE) {
                    currentState = STATE_TEXT;
                    out.append("</div>");
                }
                break;
            case '\\':
                out.append("\\");
                if (currentState == STATE_DOUBLE_QUOTE
                        || currentState == STATE_SINGLE_QUOTE) {
                    // treat as a character escape sequence
                    out.append(src.charAt(++currentIndex));
                }
                break;
            // if you want to translate tabs into spaces, uncomment the
            // following lines
            // case '\t':
            // // replace tabs with tabsize number of spaces
            // for (int i = 0; i < tabSize; i++)
            // out.append("&nbsp;&nbsp;&nbsp;&nbsp;");
            // break;
            case '*':
                out.append('*');
                if (currentState == STATE_TEXT && currentIndex > 0
                        && src.charAt(currentIndex - 1) == '/') {
                    out.insert(out.length() - ("/*").length(),
                            "<div class=\"multiLineCommentStyle\">");
                    currentState = STATE_MULTI_LINE_COMMENT;
                }
                break;
            case '/':
                out.append("/");
                if (currentState == STATE_TEXT && currentIndex > 0
                        && src.charAt(currentIndex - 1) == '/') {
                    out.insert(out.length() - ("//").length(),
                            "<div class=\"singleLineCommentStyle\">");
                    currentState = STATE_LINE_COMMENT;
                } else if (currentState == STATE_MULTI_LINE_COMMENT) {
                    out.append("</div>");
                    currentState = STATE_TEXT;
                }
                break;
            case '\r':
            case '\n':
                // end single line comments
                if (currentState == STATE_LINE_COMMENT) {
                    out.insert(out.length() - 1, "</div>");
                    currentState = STATE_TEXT;
                }
                if (currentChar == '\r' && currentIndex < src.length() - 1) {
                    out.append("\r\n");
                    ++currentIndex;
                } else
                    out.append('\n');
 
                if (enableLineNumber)
                    out.append("<div class=\"lineNumberStyle\">"
                            + (++lineNumber) + ".</div>");
                break;
            case 0:
                if (currentState == STATE_LINE_COMMENT
                        && currentIndex == (src.length() - 1))
                    out.append("</div>");
                break;
            default: // everything else
                out.append(currentChar);
            }
            identifierLength = 0;
        }
        return out.toString();
    }
 
    // test the program by reading a Java file as a String and letting the
    // program process the source code
    public static void main(String args[]) throws Exception {
        File file = new File(
                "src/JavaSyntaxHighterDemo/JavaSyntaxHighlighter.java");
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), "UTF-8"));
        StringBuffer sb = new StringBuffer();
        String temp = null;
        while ((temp = br.readLine()) != null) {
            sb.append(temp).append('\n');
        }
        String src = sb.toString();
        JavaSyntaxHighlighter jsh = new JavaSyntaxHighlighter();
        System.out.println(jsh.process(src));
    }
 
}